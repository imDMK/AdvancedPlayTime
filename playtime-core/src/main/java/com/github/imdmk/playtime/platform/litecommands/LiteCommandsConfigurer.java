package com.github.imdmk.playtime.platform.litecommands;

import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service(priority = ComponentPriority.LOWEST)
public class LiteCommandsConfigurer {

    private static final String FALLBACK_PREFIX = "AdvancedPlayTime";

    private final LiteCommandsBuilder<?, ?, ?> liteCommands;

    @Inject
    public LiteCommandsConfigurer(@NotNull Plugin plugin, @NotNull Server server) {
        this.liteCommands = LiteBukkitFactory.builder(FALLBACK_PREFIX, plugin, server);
    }

    public LiteCommandsBuilder<?, ?, ?> builder() {
        return liteCommands;
    }

    public LiteCommands<?> build() {
        return liteCommands.build();
    }
}
