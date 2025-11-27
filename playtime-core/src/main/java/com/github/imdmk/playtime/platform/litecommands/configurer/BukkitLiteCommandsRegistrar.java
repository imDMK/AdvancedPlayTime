package com.github.imdmk.playtime.platform.litecommands.configurer;

import com.github.imdmk.playtime.shared.Validator;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Bukkit-specific implementation of {@link LiteCommandsRegistrar} responsible for
 * configuring and constructing {@link LiteCommands} instances via the LiteCommands framework.
 *
 * <p>This class provides a simple builder-style API for registering commands
 * and applying additional {@link LiteCommandsBuilder} customizations before
 * the command system is initialized.</p>
 *
 * <p><strong>Usage:</strong></p>
 * <pre>{@code
 * LiteCommandsConfigurer configurer = new BukkitLiteCommandsConfigurer()
 *     .register(new MyCommand(), new OtherCommand())
 *     .configure(builder -> builder.settings().prefix("myplugin"));
 *
 * LiteCommands<CommandSender> liteCommands = configurer.create("myplugin", plugin, server);
 * }</pre>
 *
 * <p><strong>Thread-safety:</strong> This class is not thread-safe and should be configured
 * only during plugin initialization (main thread).</p>
 *
 * @see LiteCommands
 * @see LiteCommandsBuilder
 * @see LiteBukkitFactory
 */
public final class BukkitLiteCommandsRegistrar implements LiteCommandsRegistrar {

    private final List<Consumer<LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?>>> customizers = new ArrayList<>();
    private final List<Object> commands = new ArrayList<>();

    /**
     * Registers a new customization for the {@link LiteCommandsBuilder}.
     *
     * <p>Customizers can modify settings, add components, or register custom parameter resolvers.</p>
     *
     * @param customizer a consumer that customizes the LiteCommands builder (non-null)
     * @return this configurer instance for chaining
     * @throws NullPointerException if {@code customizer} is null
     */
    @Contract("_ -> this")
    @Override
    public LiteCommandsRegistrar configure(@NotNull Consumer<LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?>> customizer) {
        this.customizers.add(customizer);
        return this;
    }

    /**
     * Registers one or more command handler instances to be used by LiteCommands.
     *
     * @param commands non-null command handler instances
     * @return this configurer instance for chaining
     * @throws NullPointerException if {@code commands} is null
     */
    @Contract("_ -> this")
    @Override
    public LiteCommandsRegistrar registerCommands(@NotNull Object... commands) {
        this.commands.addAll(Arrays.asList(commands));
        return this;
    }

    /**
     * Creates and builds a fully configured {@link LiteCommands} instance.
     *
     * <p>All registered customizers are applied sequentially before building.
     * The specified commands are then registered into the resulting command manager.</p>
     *
     * @param fallbackPrefix fallback command prefix (e.g., plugin name)
     * @param plugin         Bukkit plugin instance
     * @param server         Bukkit server reference
     * @return a fully initialized {@link LiteCommands} instance
     * @throws NullPointerException if any argument is null
     */
    @Override
    public @NotNull LiteCommands<CommandSender> create(@NotNull String fallbackPrefix,
                                                       @NotNull Plugin plugin,
                                                       @NotNull Server server) {
        Validator.notNull(fallbackPrefix, "fallbackPrefix cannot be null");
        Validator.notNull(plugin, "plugin cannot be null");
        Validator.notNull(server, "server cannot be null");

        var builder = LiteBukkitFactory.builder(fallbackPrefix, plugin, server);
        for (var customizer : customizers) {
            customizer.accept(builder);
        }

        builder.commands(commands.toArray());

        return builder.build();
    }
}
