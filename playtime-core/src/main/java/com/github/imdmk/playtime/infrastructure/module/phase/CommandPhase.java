package com.github.imdmk.playtime.infrastructure.module.phase;

import com.github.imdmk.playtime.platform.litecommands.configurer.LiteCommandsRegistrar;
import org.jetbrains.annotations.NotNull;

/**
 * Functional phase interface responsible for command registration.
 * <p>
 * Implementations should declare and configure commands using the
 * provided {@link LiteCommandsRegistrar}.
 */
@FunctionalInterface
public interface CommandPhase {

    /**
     * Configures and registers commands for this module.
     *
     * @param configurer the command configurer used to register LiteCommands commands (never {@code null})
     */
    void configure(@NotNull LiteCommandsRegistrar configurer);
}
