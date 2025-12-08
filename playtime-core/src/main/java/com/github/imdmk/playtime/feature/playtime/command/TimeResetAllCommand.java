package com.github.imdmk.playtime.feature.playtime.command;

import com.github.imdmk.playtime.PlaytimeService;
import com.github.imdmk.playtime.message.MessageService;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.shared.validate.Validator;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserSaveReason;
import com.github.imdmk.playtime.user.UserService;
import com.github.imdmk.playtime.user.UserTime;
import com.github.imdmk.playtime.user.repository.UserRepository;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Command(name = "playtime reset-all")
@Permission("command.playtime.reset.all")
public final class TimeResetAllCommand {

    private static final UserSaveReason SAVE_REASON = UserSaveReason.RESET_COMMAND;

    private final Server server;
    private final PluginLogger logger;
    private final MessageService messageService;
    private final PlaytimeService playtimeService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;

    @Inject
    public TimeResetAllCommand(
            @NotNull Server server,
            @NotNull PluginLogger logger,
            @NotNull MessageService messageService,
            @NotNull PlaytimeService playtimeService,
            @NotNull UserService userService,
            @NotNull UserRepository userRepository,
            @NotNull TaskScheduler taskScheduler
    ) {
        this.server = Validator.notNull(server, "server cannot be null");
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
        this.userRepository = Validator.notNull(userRepository, "userRepository cannot be null");
        this.taskScheduler = Validator.notNull(taskScheduler, "taskScheduler cannot be null");
    }

    @Execute
    void resetAll(@Context CommandSender sender) {
        messageService.send(sender, n -> n.playtimeMessages.playerPlaytimeResetAllStarted());

        taskScheduler.runSync(this::resetOfflinePlayers);
        userRepository.findAll()
                .thenCompose(users -> {
                    if (users.isEmpty()) {
                        return CompletableFuture.completedFuture(false);
                    }

                    return CompletableFuture.allOf(users.stream()
                                    .map(this::resetUser)
                                    .toArray(CompletableFuture[]::new))
                            .thenApply(v -> true);
                })
                .whenComplete((b, e) -> {
                    if (e != null) {
                        logger.error(e, "Failed to reset playtime for all users");
                        messageService.send(sender, n -> n.playtimeMessages.playerPlaytimeResetAllFailed());
                        return;
                    }

                    messageService.send(sender, n -> n.playtimeMessages.playerPlaytimeResetAllFinished());
                });
    }

    private CompletableFuture<User> resetUser(@NotNull User user) {
        user.setPlaytime(UserTime.ZERO);
        return userService.save(user, SAVE_REASON);
    }

    private void resetOfflinePlayers() {
        Arrays.stream(server.getOfflinePlayers())
                .map(OfflinePlayer::getUniqueId)
                .forEach(playtimeService::resetTime);
    }
}
