package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.injector.annotations.Controller;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.playtime.PlayTimeAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

@Controller
final class PlayTimeSaveListener implements Listener {

    private final PluginLogger logger;
    private final PlayTimeAdapter playTimeAdapter;
    private final PlayTimeUserService userService;

    @Inject
    PlayTimeSaveListener(
            @NotNull PluginLogger logger,
            @NotNull PlayTimeAdapter playTimeAdapter,
            @NotNull PlayTimeUserService userService
    ) {
        this.logger = logger;
        this.playTimeAdapter = playTimeAdapter;
        this.userService = userService;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final PlayTime playTime = playTimeAdapter.read(player);

        userService.getOrCreate(uuid, playTime)
                .thenAccept(user -> playTimeAdapter.write(player, user.getPlayTime()))
                .exceptionally(e -> {
                    logger.error(e, "Failed to get user with uuid %s on PlayerJoinEvent", uuid);
                    return null;
                });
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final PlayTime playTime = playTimeAdapter.read(player);

        userService.setPlayTime(uuid, playTime)
                .exceptionally(e -> {
                    logger.error(e, "Failed to set user playTime with uuid %s on PlayerQuitEvent", uuid);
                    return null;
                });
    }

}
