package com.github.imdmk.playtime.feature.playtime.command;

import com.github.imdmk.playtime.PlayTimeService;
import com.github.imdmk.playtime.message.MessageService;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.time.Durations;
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

import java.time.Duration;

@Command(name = "playtime set")
@Permission("command.playtime.set")
public final class TimeSetCommand {

    private final PluginLogger logger;
    private final MessageService messageService;
    private final PlayTimeService playTimeService;
    private final UserService userService;

    @Inject
    public TimeSetCommand(
            @NotNull PluginLogger logger,
            @NotNull MessageService messageService,
            @NotNull PlayTimeService playTimeService,
            @NotNull UserService userService
    ) {
        this.logger = logger;
        this.messageService = messageService;
        this.playTimeService = playTimeService;
        this.userService = userService;
    }

    @Execute
    void setPlaytime(@Context CommandSender sender, @Arg @Async User target, @Arg Duration time) {
        final Duration normalizedTime = Durations.clamp(time);
        final UserTime newTime = UserTime.ofDuration(normalizedTime);

        target.setPlaytime(newTime);
        playTimeService.setTime(target.getUuid(), newTime);

        userService.save(target, UserSaveReason.SET_COMMAND)
                .thenAccept(v -> messageService.create()
                        .notice(n -> n.playtimeMessages.playerPlaytimeUpdated())
                        .placeholder("{PLAYER_NAME}", target.getName())
                        .placeholder("{PLAYER_PLAYTIME}", Durations.format(normalizedTime))
                        .viewer(sender)
                        .send()
                )
                .exceptionally(e -> {
                    logger.error(e, "Failed to save user on playtime set command (target=%s)", target.getName());
                    messageService.send(sender, n -> n.actionExecutionError);
                    return null;
                });
    }
}
