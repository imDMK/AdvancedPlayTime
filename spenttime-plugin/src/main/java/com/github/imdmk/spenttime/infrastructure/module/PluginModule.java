package com.github.imdmk.spenttime.infrastructure.module;

import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

/**
 * Base lifecycle contract for all SpentTime modules.
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
public interface PluginModule extends ModuleOrdered {

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
     * Default neutral order. Lower values initialize earlier.
     */
    @Override
    default int order() { return 0; }
}
