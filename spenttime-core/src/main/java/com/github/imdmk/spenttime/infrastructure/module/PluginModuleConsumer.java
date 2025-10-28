package com.github.imdmk.spenttime.infrastructure.module;

import org.jetbrains.annotations.NotNull;

/**
 * Internal functional interface used by {@link PluginModuleInitializer}
 * to execute lifecycle phases for each {@link PluginModule}.
 */
@FunctionalInterface
interface PluginModuleConsumer {

    /**
     * Performs an operation on the given module.
     *
     * @param module the module instance to operate on (never {@code null})
     * @throws Exception if the operation fails
     */
    void accept(@NotNull PluginModule module) throws Exception;
}
