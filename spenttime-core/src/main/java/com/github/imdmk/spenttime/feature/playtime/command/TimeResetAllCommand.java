package com.github.imdmk.spenttime.feature.playtime.command;

import com.github.imdmk.spenttime.PlaytimeService;
import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.shared.message.MessageService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserSaveReason;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.UserTime;
import com.github.imdmk.spenttime.user.repository.UserRepository;
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

@Command(name = "spenttime reset-all")
@Permission("command.spenttime.reset.all")
public class TimeResetAllCommand {

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
            @NotNull TaskScheduler taskScheduler) {
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
        user.setSpentTime(UserTime.ZERO);
        return userService.save(user, SAVE_REASON);
    }

    private void resetOfflinePlayers() {
        Arrays.stream(server.getOfflinePlayers())
                .map(OfflinePlayer::getUniqueId)
                .forEach(playtimeService::resetTime);
    }
}
