package com.github.imdmk.playtime.infrastructure.module.phase;

import dev.rollczi.litecommands.LiteCommandsBuilder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Functional phase interface responsible for command registration.
 * <p>
 * Implementations should declare and configure commands using the
 * provided {@link LiteCommandsBuilder}.
 */
@FunctionalInterface
public interface CommandPhase {

    /**
     * Configures and registers commands for this module.
     *
     * @param configurer the command configurer used to register LiteCommands commands (never {@code null})
     */
    void configure(@NotNull LiteCommandsBuilder<CommandSender, ?, ?> configurer);
}
