package com.github.imdmk.spenttime.platform.litecommands;

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

public class BukkitLiteCommandsConfigurer implements LiteCommandsConfigurer {

    private final List<Consumer<LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?>>> customizers = new ArrayList<>();
    private final List<Object> commands = new ArrayList<>();

    @Contract("_ -> this")
    @Override
    public LiteCommandsConfigurer configure(@NotNull Consumer<LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?>> customizer) {
        this.customizers.add(customizer);
        return this;
    }

    @Contract("_ -> this")
    @Override
    public LiteCommandsConfigurer register(@NotNull Object... commands) {
        this.commands.addAll(Arrays.asList(commands));
        return this;
    }

    @Override
    public @NotNull LiteCommands<CommandSender> create(@NotNull String fallbackPrefix, @NotNull Plugin plugin, @NotNull Server server) {
        var builder = LiteBukkitFactory.builder(fallbackPrefix, plugin, server);

        for (var customizer : this.customizers) {
            customizer.accept(builder);
        }

        builder.commands(this.commands.toArray());

        return builder.build();
    }
}
