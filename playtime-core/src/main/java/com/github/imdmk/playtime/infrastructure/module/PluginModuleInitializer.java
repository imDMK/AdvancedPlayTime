package com.github.imdmk.playtime.infrastructure.module;

import com.github.imdmk.playtime.infrastructure.database.repository.RepositoryManager;
import com.github.imdmk.playtime.platform.events.BukkitListenerRegistrar;
import com.github.imdmk.playtime.platform.gui.GuiRegistry;
import com.github.imdmk.playtime.platform.litecommands.configurer.LiteCommandsConfigurer;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.shared.Validator;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.util.List;

/**
 * Coordinates the complete initialization lifecycle of all {@link PluginModule}s.
 * <p>
 * Each phase of the lifecycle is executed sequentially on the Bukkit main thread:
 * <ol>
 *     <li>{@link #loadAndSort(List)} – loads, instantiates and sorts all modules.</li>
 *     <li>{@link #bindAll()} – exposes module resources to the dependency injection container.</li>
 *     <li>{@link #initAll()} – initializes module internals using the {@link Injector}.</li>
 *     <li>{@link #registerRepositories()} – registers repository descriptors (no database I/O).</li>
 *     <li>{@link #activateFeatures()} – activates all feature phases: tasks, listeners, commands, GUIs, afterRegister.</li>
 * </ol>
 * <p>
 * Each module is executed in isolation — exceptions from one module do not prevent other modules from being processed.
 * <p>
 * <b>Thread-safety:</b> This class is <strong>not thread-safe</strong> and must be executed exclusively on
 * the Bukkit primary thread.
 */
public final class PluginModuleInitializer {

    private final Plugin plugin;
    private final Server server;
    private final PluginLogger logger;
    private final TaskScheduler taskScheduler;
    private final RepositoryManager repositoryManager;
    private final BukkitListenerRegistrar bukkitListenerRegistrar;
    private final LiteCommandsConfigurer liteCommandsConfigurer;
    private final GuiRegistry guiRegistry;
    private final PluginModuleRegistry moduleRegistry;
    private final Injector injector;

    private State state = State.NEW;

    public PluginModuleInitializer(
            @NotNull Plugin plugin,
            @NotNull Server server,
            @NotNull PluginLogger logger,
            @NotNull TaskScheduler taskScheduler,
            @NotNull RepositoryManager repositoryManager,
            @NotNull BukkitListenerRegistrar bukkitListenerRegistrar,
            @NotNull LiteCommandsConfigurer liteCommandsConfigurer,
            @NotNull GuiRegistry guiRegistry,
            @NotNull PluginModuleRegistry moduleRegistry,
            @NotNull Injector injector) {
        this.plugin = Validator.notNull(plugin, "plugin cannot be null");
        this.server = Validator.notNull(server, "server cannot be null");
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.taskScheduler = Validator.notNull(taskScheduler, "taskScheduler cannot be null");
        this.repositoryManager = Validator.notNull(repositoryManager, "repositoryManager cannot be null");
        this.bukkitListenerRegistrar = Validator.notNull(bukkitListenerRegistrar, "bukkitListenerRegistrar cannot be null");
        this.liteCommandsConfigurer = Validator.notNull(liteCommandsConfigurer, "liteCommandsConfigurer cannot be null");
        this.guiRegistry = Validator.notNull(guiRegistry, "guiRegistry cannot be null");
        this.moduleRegistry = Validator.notNull(moduleRegistry, "moduleRegistry cannot be null");
        this.injector = Validator.notNull(injector, "injector cannot be null");
    }

    /**
     * Loads all provided module types, instantiates them using the {@link Injector},
     * and sorts them by their {@link PluginModule#order()} value.
     * <p>
     * This phase must be executed first, before any other lifecycle phase.
     *
     * @param types a list of module classes to load and register
     * @param <T>   the module type extending {@link PluginModule}
     * @throws IllegalStateException if invoked out of order
     */
    public <T extends PluginModule> void loadAndSort(@NotNull List<Class<? extends T>> types) {
        Validator.notNull(types, "types cannot be null");

        ensureMainThread();
        ensureState(State.NEW, "loadAndSort");

        moduleRegistry.setModuleTypes(types);
        moduleRegistry.instantiateAndSort(injector);

        state = State.LOADED;
    }

    /**
     * Invokes {@link PluginModule#bind(org.panda_lang.utilities.inject.Resources)} on all modules.
     * <p>
     * This exposes shared resources into the dependency injection context
     * before the {@link #initAll()} phase.
     *
     * @throws IllegalStateException if executed before {@link #loadAndSort(List)}
     */
    public void bindAll() {
        ensureMainThread();
        ensureState(State.LOADED, "bindAll");

        forEachModule("bindAll", m -> m.bind(injector.getResources()));
        state = State.BOUND;
    }

    /**
     * Invokes {@link PluginModule#init(Injector)} on all modules.
     * <p>
     * This allows each module to initialize its internal state and prepare dependencies.
     *
     * @throws IllegalStateException if executed before {@link #bindAll()}
     */
    public void initAll() {
        ensureMainThread();
        ensureState(State.BOUND, "initAll");

        forEachModule("initAll", m -> m.init(injector));
        state = State.INITIALIZED;
    }

    /**
     * Invokes {@link PluginModule#repositories(Injector)} for each module to register
     * repository descriptors with the {@link RepositoryManager}.
     * <p>
     * This phase must not perform database I/O. Only metadata or repository registration
     * should occur here.
     *
     * @throws IllegalStateException if executed before {@link #initAll()}
     */
    public void registerRepositories() {
        ensureMainThread();
        ensureState(State.INITIALIZED, "registerRepositories");

        forEachModule("registerRepositories", m -> m.repositories(injector).register(repositoryManager));

        state = State.REPOS_REGISTERED;
    }

    /**
     * Activates all feature phases on every {@link PluginModule}.
     * <p>
     * Executes in the following order per module:
     * <ul>
     *     <li>{@link PluginModule#tasks(Injector)}</li>
     *     <li>{@link PluginModule#listeners(Injector)}</li>
     *     <li>{@link PluginModule#commands(Injector)}</li>
     *     <li>{@link PluginModule#guis(Injector)}</li>
     *     <li>{@link PluginModule#afterRegister(Plugin, Server, Injector)}</li>
     * </ul>
     * <p>
     * Each phase is executed safely; an exception in one phase or module
     * will be logged and will not interrupt the rest of the lifecycle.
     *
     * @throws IllegalStateException if executed before {@link #registerRepositories()}
     */
    public void activateFeatures() {
        ensureMainThread();
        ensureState(State.REPOS_REGISTERED, "activateFeatures");

        forEachModule("activateFeatures", m -> {
            m.tasks(injector).schedule(taskScheduler);
            m.listeners(injector).register(bukkitListenerRegistrar);
            m.commands(injector).configure(liteCommandsConfigurer);
            m.guis(injector).register(guiRegistry);
            m.afterRegister(plugin, server, injector);
        });

        state = State.FEATURES_ACTIVATED;
    }

    /**
     * Iterates over all registered modules and executes the provided consumer safely.
     * Exceptions from individual modules are caught and logged without halting execution.
     *
     * @param phase          name of the phase for logging
     * @param moduleConsumer the operation to perform for each module
     */
    private void forEachModule(@NotNull String phase, @NotNull PluginModuleConsumer moduleConsumer) {
        for (final PluginModule m : moduleRegistry.modules()) {
            try {
                moduleConsumer.accept(m);
            } catch (Throwable t) {
                logger.error(t, "%s phase failed for module %s", phase, m.getClass().getName());
            }
        }
    }

    /**
     * Ensures the initializer is in the expected {@link State} before executing a phase.
     *
     * @param required the required state
     * @param op       the operation name (for error messages)
     * @throws IllegalStateException if the current state does not match
     */
    private void ensureState(@NotNull State required, @NotNull String op) {
        if (state != required) {
            throw new IllegalStateException(op + " requires state " + required + ", but was " + state);
        }
    }

    /**
     * Verifies that the operation is being executed on the Bukkit main thread.
     *
     * @throws IllegalStateException if called from an asynchronous thread
     */
    private void ensureMainThread() {
        if (!server.isPrimaryThread()) {
            throw new IllegalStateException("PluginModuleInitializer must run on Bukkit main thread");
        }
    }

    /** Represents the current initialization phase of the module lifecycle. */
    private enum State {
        NEW,
        LOADED,
        BOUND,
        INITIALIZED,
        REPOS_REGISTERED,
        FEATURES_ACTIVATED
    }
}
