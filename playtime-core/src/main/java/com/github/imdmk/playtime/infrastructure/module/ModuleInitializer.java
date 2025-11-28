package com.github.imdmk.playtime.infrastructure.module;

import com.github.imdmk.playtime.shared.Validator;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.util.List;
import java.util.function.Consumer;

/**
 * Coordinates the entire lifecycle of all {@link Module} instances.
 *
 * <p>The initializer executes modules through a strict, ordered pipeline:
 * <ol>
 *     <li>{@link #loadAndSort(List)} – instantiation and deterministic ordering,</li>
 *     <li>{@link #bindAll()} – DI resource binding phase,</li>
 *     <li>{@link #initAll()} – internal module initialization,</li>
 *     <li>{@link #registerRepositories()} – repository descriptor registration,</li>
 *     <li>{@link #activateFeatures()} – tasks, listeners, commands, GUIs, placeholders, hooks.</li>
 * </ol>
 *
 * <p>Each step is validated against an internal state machine to enforce correct order and avoid
 * partially initialized modules. All operations run exclusively on the Bukkit main thread.</p>
 *
 * <p>Errors thrown by individual modules never abort the lifecycle — they are logged and the
 * pipeline continues for remaining modules.</p>
 */
public final class ModuleInitializer {

    private final ModuleContext context;
    private final ModuleRegistry registry;
    private final Injector injector;

    private State state = State.NEW;

    /**
     * Creates a new module initializer.
     *
     * @param context  shared runtime services accessible to modules
     * @param registry module registry used for instantiation and lookup
     * @param injector dependency injection container used during load/init
     */
    public ModuleInitializer(
            @NotNull ModuleContext context,
            @NotNull ModuleRegistry registry,
            @NotNull Injector injector) {
        this.context = Validator.notNull(context, "context cannot be null");
        this.registry = Validator.notNull(registry, "moduleRegistry cannot be null");
        this.injector = Validator.notNull(injector, "injector cannot be null");
    }

    /**
     * Instantiates and sorts all module types.
     * Must be executed first in the module lifecycle.
     */
    public <T extends Module> void loadAndSort(@NotNull List<Class<? extends T>> types) {
        Validator.notNull(types, "types cannot be null");

        ensureMainThread();
        ensureState(State.NEW, "loadAndSort");

        registry.setModuleTypes(types);
        registry.instantiateAndSort(injector);

        state = State.LOADED;
    }

    /**
     * Executes the DI binding phase for all modules.
     */
    public void bindAll() {
        ensureMainThread();
        ensureState(State.LOADED, "bindAll");

        forEachModule("bindAll", m -> m.bind(injector.getResources()));
        state = State.BOUND;
    }

    /**
     * Invokes the initialization phase for all modules.
     */
    public void initAll() {
        ensureMainThread();
        ensureState(State.BOUND, "initAll");

        forEachModule("initAll", m -> m.init(injector));
        state = State.INITIALIZED;
    }

    /**
     * Registers repository metadata for all modules.
     * Does not perform database I/O.
     */
    public void registerRepositories() {
        ensureMainThread();
        ensureState(State.INITIALIZED, "registerRepositories");

        forEachModule("registerRepositories",
                m -> m.repositories(injector).register(context.repositoryManager()));

        state = State.REPOS_REGISTERED;
    }

    /**
     * Activates all runtime features:
     * tasks, listeners, commands, GUIs, placeholders, and after-register hooks.
     */
    public void activateFeatures() {
        ensureMainThread();
        ensureState(State.REPOS_REGISTERED, "activateFeatures");

        forEachModule("activateFeatures", m -> {
            m.tasks(injector).schedule(context.taskScheduler());
            m.listeners(injector).register(context.listenerRegistrar());
            m.commands(injector).configure(context.liteCommandsRegistrar());
            m.guis(injector).register(context.guiRegistry());
            m.placeholders(injector).register(context.placeholderAdapter());
            m.afterRegister(context.plugin(), context.server(), injector);
        });

        state = State.FEATURES_ACTIVATED;
    }

    /**
     * Internal helper executing a phase for each module,
     * catching and logging exceptions from individual modules.
     */
    private void forEachModule(@NotNull String phase, @NotNull Consumer<Module> moduleConsumer) {
        for (final Module m : registry.modules()) {
            try {
                moduleConsumer.accept(m);
            } catch (Throwable t) {
                context.logger().error(t, "%s phase failed for module %s", phase, m.getClass().getName());
            }
        }
    }

    /** Validates the current initializer state. */
    private void ensureState(@NotNull State required, @NotNull String op) {
        if (state != required) {
            throw new IllegalStateException(op + " requires state " + required + ", but was " + state);
        }
    }

    /** Ensures execution on the Bukkit main thread. */
    private void ensureMainThread() {
        if (!context.server().isPrimaryThread()) {
            throw new IllegalStateException("PluginModuleInitializer must run on Bukkit main thread");
        }
    }

    /** Internal lifecycle states used to validate the correct execution order. */
    private enum State {
        NEW,
        LOADED,
        BOUND,
        INITIALIZED,
        REPOS_REGISTERED,
        FEATURES_ACTIVATED
    }
}
