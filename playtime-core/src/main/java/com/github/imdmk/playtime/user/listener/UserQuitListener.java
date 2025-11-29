package com.github.imdmk.playtime.user.listener;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.user.UserSaveReason;
import com.github.imdmk.playtime.user.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class UserQuitListener implements Listener {

    private static final Duration SAVE_TIMEOUT = Duration.ofSeconds(2);
    private static final UserSaveReason SAVE_REASON = UserSaveReason.PLAYER_LEAVE;

    private final PluginLogger logger;
    private final UserService userService;

    @Inject
    public UserQuitListener(@NotNull PluginLogger logger, @NotNull UserService userService) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final UUID uuid = player.getUniqueId();
        final String name = player.getName();

        userService.findCachedByUuid(uuid).ifPresent(user -> userService.save(user, SAVE_REASON)
                .orTimeout(SAVE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .whenComplete((u, e) -> {
                    if (e != null) {
                        logger.error(e, "Failed to save user on quit %s (%s)", name, uuid);
                    }
                })
        );
    }
}
