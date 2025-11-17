package com.github.imdmk.spenttime.user.listener;

import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserFactory;
import com.github.imdmk.spenttime.user.UserSaveReason;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class UserJoinListener implements Listener {

    private static final Duration FIND_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration SAVE_TIMEOUT = Duration.ofSeconds(2);
    private static final UserSaveReason SAVE_REASON = UserSaveReason.JOIN;

    private final Server server;
    private final PluginLogger logger;
    private final UserService userService;
    private final UserFactory userFactory;
    private final TaskScheduler taskScheduler;

    @Inject
    public UserJoinListener(
            @NotNull Server server,
            @NotNull PluginLogger logger,
            @NotNull UserService userService,
            @NotNull UserFactory userFactory,
            @NotNull TaskScheduler taskScheduler) {
        this.server = Validator.notNull(server, "server cannot be null");
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
        this.userFactory = Validator.notNull(userFactory, "userFactory cannot be null");
        this.taskScheduler = Validator.notNull(taskScheduler, "taskScheduler cannot be null");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        handlePlayerJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerReload(@NotNull ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) {
            server.getOnlinePlayers().forEach(this::handlePlayerJoin);
        }
    }

    private void handlePlayerJoin(@NotNull Player player) {
        final UUID uuid = player.getUniqueId();

        userService.findByUuid(uuid)
                .orTimeout(FIND_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .whenComplete((optional, e) -> {
                    if (e != null) {
                        logger.error(e, "Failed to load user on join uuid=%s", uuid);
                        return;
                    }

                    taskScheduler.runSync(() -> handleLoadedUser(player, optional));
                });
    }

    private void handleLoadedUser(@NotNull Player player, @NotNull Optional<User> optionalUser) {
        optionalUser.ifPresentOrElse(
                user -> handleExistingUser(player, user),
                () -> handleNewUser(player)
        );
    }

    private void handleNewUser(@NotNull Player player) {
        final User user = userFactory.createFrom(player);
        saveUser(user, "on join (new)");
    }

    private void handleExistingUser(@NotNull Player player, @NotNull User user) {
        final String name = player.getName();

        if (!updateNameIfChanged(user, name)) {
            return;
        }

        saveUser(user, "on join (update name)");
    }

    private void saveUser(@NotNull User user, @NotNull String context) {
        userService.save(user, SAVE_REASON)
                .orTimeout(SAVE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .whenComplete((r, e) -> {
                    if (e != null) {
                        logger.error(e, "Failed to save user %s uuid=%s", context, user.getUuid());
                    }
                });
    }

    private boolean updateNameIfChanged(@NotNull User user, @NotNull String name) {
        final String oldName = user.getName();
        if (oldName.equals(name)) {
            return false;
        }

        user.setName(name);
        return true;
    }
}
