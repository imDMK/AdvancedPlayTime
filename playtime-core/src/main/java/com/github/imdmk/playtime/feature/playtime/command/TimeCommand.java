package com.github.imdmk.playtime.feature.playtime.command;

import com.github.imdmk.playtime.PlaytimeService;
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.shared.message.MessageService;
import com.github.imdmk.playtime.shared.time.Durations;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserTime;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Command(name = "playtime")
public final class TimeCommand {

    private final MessageService messageService;
    private final PlaytimeService playtimeService;

    @Inject
    public TimeCommand(@NotNull MessageService messageService, @NotNull PlaytimeService playtimeService) {
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
    }

    @Execute
    @Permission("command.playtime")
    void selfPlaytime(@Context Player viewer) {
        final UserTime time = playtimeService.getTime(viewer.getUniqueId());

        messageService.create()
                .notice(n -> n.playtimeMessages.playerPlaytimeSelf())
                .viewer(viewer)
                .placeholder("{PLAYER_PLAYTIME}", Durations.format(time.toDuration()))
                .send();
    }

    @Execute
    @Permission("command.playtime.target")
    void targetPlaytime(@Context Player viewer, @Arg @Async User target) {
        final UserTime time = playtimeService.getTime(target.getUuid());

        messageService.create().notice(n -> n.playtimeMessages.playerPlaytimeTarget())
                .viewer(viewer)
                .placeholder("{PLAYER_NAME}", target.getName())
                .placeholder("{PLAYER_PLAYTIME}", Durations.format(time.toDuration()))
                .send();
    }
}
