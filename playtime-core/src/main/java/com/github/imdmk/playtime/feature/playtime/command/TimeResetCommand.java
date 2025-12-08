package com.github.imdmk.playtime.feature.playtime.command;

import com.github.imdmk.playtime.message.MessageService;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.validate.Validator;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserSaveReason;
import com.github.imdmk.playtime.user.UserService;
import com.github.imdmk.playtime.user.UserTime;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Command(name = "playtime reset")
@Permission("command.playtime.reset")
public final class TimeResetCommand {

    private static final UserSaveReason SAVE_REASON = UserSaveReason.RESET_COMMAND;

    private final PluginLogger logger;
    private final MessageService messageService;
    private final UserService userService;

    @Inject
    public TimeResetCommand(
            @NotNull PluginLogger logger,
            @NotNull MessageService messageService,
            @NotNull UserService userService
    ) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
    }

    @Execute
    void reset(@Context CommandSender sender, @Arg @Async User target) {
        target.setPlaytime(UserTime.ZERO);

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
