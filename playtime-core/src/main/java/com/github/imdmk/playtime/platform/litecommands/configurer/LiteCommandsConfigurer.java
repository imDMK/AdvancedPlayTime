package com.github.imdmk.playtime.platform.litecommands.configurer;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Thin facade over {@link LiteCommandsBuilder} to keep LiteCommands usage
 * behind a simple, fluent configuration interface.
 *
 * <p>Intent:
 * <ul>
 *   <li>accept external customizers,</li>
 *   <li>register command handlers,</li>
 *   <li>create a ready {@link LiteCommands} instance.</li>
 * </ul>
 *
 * <p><b>Threading:</b> should be used on the server main thread.
 * Implementations are expected to be non-blocking and side-effect free
 * until {@link #create(String, Plugin, Server)} is invoked.</p>
 */
public interface LiteCommandsConfigurer {

    /**
     * Applies a low-level customizer to the underlying {@link LiteCommandsBuilder}.
     *
     * @param customizer a callback that configures the builder
     * @return this configurer for fluent chaining
     */
    LiteCommandsConfigurer configure(
            @NotNull Consumer<LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?>> customizer
    );

    /**
     * Registers command handler instances to be picked up by LiteCommands.
     *
     * @param commands command handler objects
     * @return this configurer for fluent chaining
     */
    LiteCommandsConfigurer registerCommands(@NotNull Object... commands);

    /**
     * Builds a {@link LiteCommands} instance for Bukkit.
     *
     * @param fallbackPrefix fallback command prefix (non-null)
     * @param plugin         owning plugin (non-null)
     * @param server         Bukkit server (non-null)
     * @return initialized {@link LiteCommands} ready to use
     */
    @NotNull
    LiteCommands<CommandSender> create(@NotNull String fallbackPrefix,
                                       @NotNull Plugin plugin,
                                       @NotNull Server server);
}
