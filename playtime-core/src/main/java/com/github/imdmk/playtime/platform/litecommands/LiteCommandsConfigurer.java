package com.github.imdmk.playtime.platform.litecommands;

import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeInitializeEvent;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service(priority = ComponentPriority.LOWEST)
public final class LiteCommandsConfigurer {

    private static final String FALLBACK_PREFIX = "AdvancedPlayTime";

    private final LiteCommandsBuilder<?, ?, ?> builder;
    private LiteCommands<?> liteCommands;

    @Inject
    LiteCommandsConfigurer(@NotNull Plugin plugin, @NotNull Server server) {
        this.builder = LiteBukkitFactory.builder(FALLBACK_PREFIX, plugin, server);
    }

    public LiteCommandsBuilder<?, ?, ?> builder() {
        return builder;
    }

    public LiteCommands<?> liteCommands() {
        if (liteCommands == null) {
            throw new IllegalStateException("LiteCommands not initialized yet");
        }
        return liteCommands;
    }

    @Subscribe(event = PlayTimeInitializeEvent.class)
    private void onInitialize() {
        this.liteCommands = builder.build();
    }
}

