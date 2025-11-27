package com.github.imdmk.playtime.infrastructure.module;

import com.github.imdmk.playtime.infrastructure.module.phase.CommandPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.GuiPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.ListenerPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.PlaceholderPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.RepositoryPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.TaskPhase;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

/**
 * Base lifecycle contract for all PlayTime modules.
 *
 * <p>Lifecycle phases:
 * <ol>
 *   <li><b>bind(Resources)</b> – expose and wire resources into the DI context.</li>
 *   <li><b>init(Injector)</b> – initialize internal state; safe to use bound resources.</li>
 * </ol>
 * The manager guarantees {@code bind()} is called before {@code init()}.</p>
 *
 * <p>Threading: modules are initialized on the server main thread unless documented otherwise.
 * Implementations should avoid long blocking operations in {@code bind()} and {@code init()}.</p>
 */
public interface Module extends Ordered {

    /**
     * Binds resources into the DI container. This phase happens before {@link #init(Injector)}.
     *
     * @param resources DI resources registry (never {@code null})
     */
    void bind(@NotNull Resources resources);

    /**
     * Initializes the module. At this point, all resources are already bound.
     *
     * @param injector DI injector (never {@code null})
     */
    void init(@NotNull Injector injector);

    /**
     * Repositories registration phase (optional).
     */
    default RepositoryPhase repositories(@NotNull Injector injector) { return repositoryManager -> {}; }

    /**
     * Task scheduling phase (optional).
     */
    default TaskPhase tasks(@NotNull Injector injector) { return taskScheduler -> {}; }

    /**
     * Listener registration phase (optional).
     */
    default ListenerPhase listeners(@NotNull Injector injector) { return listenerRegistrar -> {}; }

    /**
     * Command registration phase (optional).
     */
    default CommandPhase commands(@NotNull Injector injector) { return liteCommandsConfigurer -> {}; }

    /**
     * Gui's registration phase (optional).
     */
    default GuiPhase guis(@NotNull Injector injector) { return guiRegistry -> {}; }

    /**
     * Placeholder's registration phase (optional).
     */
    default PlaceholderPhase placeholders(@NotNull Injector injector) { return placeholderRegistry -> {}; }

    /**
     * Final hook invoked after all registrations of this feature are complete.
     * Runs on the main server thread.
     */
    default void afterRegister(@NotNull Plugin plugin, @NotNull Server server, @NotNull Injector injector) {}

    /**
     * Default neutral order. Lower values initialize earlier.
     */
    @Override
    default int order() { return 0; }
}
