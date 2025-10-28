package com.github.imdmk.spenttime.infrastructure.module.phase;

import com.github.imdmk.spenttime.platform.litecommands.LiteCommandsConfigurer;
import org.jetbrains.annotations.NotNull;

/**
 * Functional phase interface responsible for command registration.
 * <p>
 * Implementations should declare and configure commands using the
 * provided {@link LiteCommandsConfigurer}.
 */
@FunctionalInterface
public interface CommandPhase {

    /**
     * Configures and registers commands for this module.
     *
     * @param configurer the command configurer used to register LiteCommands commands (never {@code null})
     */
    void configure(@NotNull LiteCommandsConfigurer configurer);
}
