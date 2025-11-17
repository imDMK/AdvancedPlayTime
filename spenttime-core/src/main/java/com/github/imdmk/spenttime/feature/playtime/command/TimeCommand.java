package com.github.imdmk.spenttime.feature.playtime.command;

import com.github.imdmk.spenttime.PlaytimeService;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.shared.message.MessageService;
import com.github.imdmk.spenttime.shared.time.Durations;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserTime;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Command(name = "spenttime")
public final class TimeCommand {

    private final MessageService messageService;
    private final PlaytimeService playtimeService;
    private final TaskScheduler taskScheduler;

    @Inject
    public TimeCommand(
            @NotNull MessageService messageService,
            @NotNull PlaytimeService playtimeService,
            @NotNull TaskScheduler taskScheduler) {
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
        this.taskScheduler = Validator.notNull(taskScheduler, "taskScheduler cannot be null");
    }

    @Execute
    @Permission("command.spenttime")
    void selfPlaytime(@Context Player viewer) {
        final UserTime time = playtimeService.getTime(viewer.getUniqueId());

        messageService.create()
                .notice(n -> n.playtimeMessages.playerPlaytimeSelf())
                .viewer(viewer)
                .placeholder("{PLAYER_PLAYTIME}", Durations.format(time.toDuration()))
                .send();
    }

    @Execute
    @Permission("command.spenttime.target")
    void targetPlaytime(@Context Player viewer, @Arg @Async User target) {
        final UserTime time = playtimeService.getTime(target.getUuid());

        messageService.create().notice(n -> n.playtimeMessages.playerPlaytimeTarget())
                .viewer(viewer)
                .placeholder("{PLAYER_NAME}", target.getName())
                .placeholder("{PLAYER_PLAYTIME}", Durations.format(time.toDuration()))
                .send();
    }
}
