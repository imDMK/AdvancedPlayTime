package com.github.imdmk.spenttime.platform.listener.user;

import com.github.imdmk.spenttime.PlaytimeService;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.UserTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class UserJoinListener implements Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserJoinListener.class);

    private static final Duration FIND_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration SAVE_TIMEOUT = Duration.ofSeconds(2);

    @Inject private UserService userService;
    @Inject private PlaytimeService playtimeService;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final UUID uuid = player.getUniqueId();
        final String name = player.getName();

        this.userService.findByUuid(uuid)
                .orTimeout(FIND_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(optional -> optional.orElseGet(() -> createUser(uuid, name)))
                // 2) compute changes
                .thenCompose(user -> {
                    boolean updatedName = this.updateNameIfNeeds(user, name);
                    boolean updatedTime = this.updateTimeIfNeeds(user);

                    if (!updatedName && !updatedTime) {
                        return CompletableFuture.completedFuture(user);
                    }

                    return this.userService.save(user)
                            .orTimeout(SAVE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
                })
                .whenComplete((u, e) -> {
                    if (e != null) {
                        LOGGER.error("Could not find user {}", name, e);
                    }
                });
    }

    private boolean updateNameIfNeeds(@NotNull User user, @NotNull String name) {
        Validator.notNull(user, "user cannot be null");
        Validator.notNull(name, "name cannot be null");

        String oldName = user.getName();
        if (oldName.equals(name)) {
            return false;
        }

        user.setName(name);
        return true;
    }

    private boolean updateTimeIfNeeds(@NotNull User user) {
        Validator.notNull(user, "user cannot be null");

        final UserTime userTime = this.playtimeService.getTime(user.getUuid());
        if (user.getSpentTime().equals(userTime)) {
            return false;
        }

        user.setSpentTime(userTime);
        return true;
    }

    private @NotNull User createUser(@NotNull UUID uuid, @NotNull String name) {
        Validator.notNull(uuid, "uuid cannot be null");
        Validator.notNull(name, "name cannot be null");

        final UserTime userTime = this.playtimeService.getTime(uuid);
        return new User(uuid, name, userTime);
    }
}
