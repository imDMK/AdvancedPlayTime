package com.github.imdmk.spenttime.infrastructure.module;

import com.github.imdmk.spenttime.platform.gui.GuiRegistry;
import com.github.imdmk.spenttime.platform.litecommands.LiteCommandsConfigurer;
import com.github.imdmk.spenttime.infrastructure.ormlite.RepositoryManager;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.util.function.Consumer;

/**
 * Feature module – optional, higher-level functionality layered on top of cores.
 *
 * <p>Manager executes phases in this order for features:
 * repositories → tasks → listeners → commands → afterRegister.</p>
 *
 * <p>Each phase returns a {@link Consumer} to be applied by the manager,
 * with a no-op default implementation.</p>
 */
public interface PluginModuleFeature extends PluginModule {

    /**
     * Repositories registration phase (optional).
     */
    default Consumer<RepositoryManager> repositories(@NotNull Injector injector) { return manager -> {}; }

    /**
     * Task scheduling phase (optional).
     */
    default Consumer<TaskScheduler> tasks(@NotNull Injector injector) { return taskScheduler -> {}; }

    /**
     * Listener registration phase (optional).
     */
    default Consumer<Plugin> listeners(@NotNull Injector injector) { return plugin -> {}; }

    /**
     * Command registration phase (optional).
     */
    default Consumer<LiteCommandsConfigurer> commands(@NotNull Injector injector) { return builder -> {}; }

    /**
     * Gui's registration phase (optional).
     */
    default Consumer<GuiRegistry> guis(@NotNull Injector injector) { return registry -> {}; }

    /**
     * Final hook invoked after all registrations of this feature are complete.
     * Runs on the main server thread.
     */
    default void afterRegister(@NotNull Plugin plugin, @NotNull Server server, @NotNull Injector injector) {}

    @Override
    default int order() {
        return 1;
    }
}
