package com.github.imdmk.playtime.user;

import com.github.imdmk.playtime.PlaytimeService;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.scheduler.PluginTask;
import com.github.imdmk.playtime.shared.Validator;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;

final class UserSaveTask implements PluginTask {

    private static final Duration INITIAL_DELAY = Duration.ofMinutes(30);
    private static final Duration INTERVAL = Duration.ofMinutes(30);

    private final Server server;
    private final PluginLogger logger;
    private final PlaytimeService playtimeService;
    private final UserService userService;

    @Inject
    UserSaveTask(
            @NotNull Server server,
            @NotNull PluginLogger logger,
            @NotNull PlaytimeService playtimeService,
            @NotNull UserService userService
    ) {
        this.server = Validator.notNull(server, "server cannot be null");
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.playtimeService = Validator.notNull(playtimeService, "playtime cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
    }

    @Override
    public void run() {
        for (final Player player : server.getOnlinePlayers()) {
            userService.findCachedByUuid(player.getUniqueId())
                    .ifPresent(this::updateAndSaveUser);
        }
    }

    private void updateAndSaveUser(@NotNull User user) {
        UserTime time = playtimeService.getTime(user.getUuid());
        user.setPlaytime(time);

        userService.save(user, UserSaveReason.SCHEDULED_SAVE)
                .exceptionally(e -> {
                    logger.error(e, "Failed to perform scheduled save for user %s (%s)", user.getName(), user.getUuid());
                    return null;
                });
    }

    @Override
    public Duration delay() {
        return INITIAL_DELAY;
    }

    @Override
    public Duration period() {
        return INTERVAL;
    }
}
