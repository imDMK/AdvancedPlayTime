package com.github.imdmk.playtime.core.platform.litecommands;

import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeInitializeEvent;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeShutdownEvent;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service(priority = ComponentPriority.LOWEST)
public final class LiteCommandsConfigurer {

    private static final String FALLBACK_PREFIX = "AdvancedPlayTime";

    private final LiteCommandsBuilder<?, ?, ?> builder;
    private LiteCommands<?> liteCommands;

    @Inject
    LiteCommandsConfigurer(Plugin plugin, Server server) {
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
    private void initialize() {
        this.liteCommands = builder.build();
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        liteCommands.unregister();
    }
}

