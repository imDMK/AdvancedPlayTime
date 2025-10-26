package com.github.imdmk.spenttime.infrastructure.module;

import com.github.imdmk.spenttime.Validator;
import com.github.imdmk.spenttime.infrastructure.ormlite.RepositoryManager;
import com.github.imdmk.spenttime.platform.gui.GuiRegistry;
import com.github.imdmk.spenttime.platform.litecommands.LiteCommandsConfigurer;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.List;
import java.util.function.Consumer;

/**
 * Coordinates the initialization lifecycle of all {@link PluginModule}s.
 *
 * <p><strong>Thread-safety:</strong> Not thread-safe. Must be used on the Bukkit main thread.</p>
 *
 * <p>Lifecycle phases:</p>
 * <ol>
 *   <li>bind – expose resources to DI</li>
 *   <li>init – module initialization</li>
 *   <li>registerRepositories – register repo descriptors</li>
 *   <li>activateFeatures – tasks → listeners → commands → guis → afterRegister</li>
 * </ol>
 */
public final class PluginModuleInitializer {

    // Environment
    @Inject private Plugin plugin;
    @Inject private Server server;
    @Inject private TaskScheduler taskScheduler;
    @Inject private RepositoryManager repositoryManager;
    @Inject private LiteCommandsConfigurer liteCommandsConfigurer;
    @Inject private GuiRegistry guiRegistry;
    @Inject private Injector injector;
    @Inject private PluginModuleRegistry moduleRegistry;

    private boolean loaded;

    /** Loads and sorts all module classes, instantiating them via the injector. */
    public <T extends PluginModule> void loadAndSort(@NotNull List<Class<? extends T>> types) {
        Validator.notNull(types, "types cannot be null");
        moduleRegistry.setModuleTypes(types);
        moduleRegistry.instantiateAndSort(injector);
        loaded = true;
    }

    /** Calls {@link PluginModule#bind(Resources)} on all modules. */
    public void bindAll() {
        ensureLoaded();

        final Resources resources = injector.getResources();
        for (PluginModule m : moduleRegistry.modules()) {
            m.bind(resources);
        }
    }

    /** Calls {@link PluginModule#init(Injector)} on all modules. */
    public void initAll() {
        ensureLoaded();

        for (PluginModule m : moduleRegistry.modules()) {
            m.init(injector);
        }
    }

    /** Registers repository descriptors from core and feature modules (no DB I/O). */
    public void registerRepositories() {
        ensureLoaded();

        for (PluginModule m : moduleRegistry.modules()) {
            if (m instanceof PluginModuleCore core) {
                invokePhase(core.repositories(injector), repositoryManager);
            } else if (m instanceof PluginModuleFeature feature) {
                invokePhase(feature.repositories(injector), repositoryManager);
            }
        }
    }

    /** Activates all {@link PluginModuleFeature}s after repositories have been started externally. */
    public void activateFeatures() {
        ensureLoaded();

        for (PluginModule module : moduleRegistry.modules()) {
            if (!(module instanceof PluginModuleFeature feature)) {
                continue;
            }

            invokePhase(feature.tasks(injector), taskScheduler);
            invokePhase(feature.listeners(injector), plugin);
            invokePhase(feature.commands(injector), liteCommandsConfigurer);
            invokePhase(feature.guis(injector), guiRegistry);
            feature.afterRegister(plugin, server, injector);
        }
    }

    private static <A> void invokePhase(@NotNull Consumer<A> consumer, @NotNull A arg) {
        consumer.accept(arg);
    }

    private void ensureLoaded() {
        if (!loaded) {
            throw new IllegalStateException("loadAndSort(...) must be called before this phase.");
        }
    }
}
