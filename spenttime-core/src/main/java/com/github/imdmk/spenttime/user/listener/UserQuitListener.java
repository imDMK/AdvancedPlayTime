package com.github.imdmk.spenttime.user.listener;

import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.user.UserSaveReason;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class UserQuitListener implements Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserQuitListener.class);
    private static final Duration SAVE_TIMEOUT = Duration.ofSeconds(2);
    private static final UserSaveReason SAVE_REASON = UserSaveReason.LEAVE;

    private final UserService userService;

    @Inject
    public UserQuitListener(@NotNull UserService userService) {
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
                        LOGGER.error("Failed to save user on quit {} ({})", name, uuid, e);
                    }
                })
        );
    }
}
