package com.github.imdmk.spenttime.feature.playtime.command;

import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.shared.message.MessageService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserSaveReason;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.UserTime;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Command(name = "spenttime reset")
@Permission("command.spenttime.reset")
public class TimeResetCommand {

    private static final UserSaveReason SAVE_REASON = UserSaveReason.RESET_COMMAND;

    private final PluginLogger logger;
    private final MessageService messageService;
    private final UserService userService;

    @Inject
    public TimeResetCommand(
            @NotNull PluginLogger logger,
            @NotNull MessageService messageService,
            @NotNull UserService userService) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
    }

    @Execute
    void reset(@Context CommandSender sender, @Arg @Async User target) {
        target.setSpentTime(UserTime.ZERO);

        userService.save(target, SAVE_REASON)
                .thenAccept(user -> messageService.create()
                        .viewer(sender)
                        .notice(n -> n.playtimeMessages.playerPlaytimeReset())
                        .placeholder("{PLAYER_NAME}", user.getName())
                        .send())
                .exceptionally(e -> {
                    logger.error(e, "An error occurred while trying to reset user playtime");
                    messageService.send(sender, n -> n.actionExecutionError);
                    return null;
                });
    }
}
