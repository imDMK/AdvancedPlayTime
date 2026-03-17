package com.github.imdmk.playtime.core.feature.playtime.command;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.feature.playtime.PlayTimeService;
import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.core.message.MessageService;
import com.github.imdmk.playtime.core.time.DurationService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;

@LiteCommand
@Command(name = "playtime set")
@Permission(PlayTimeCommandPermissions.PLAYTIME_SET)
public final class PlayTimeSetCommand {

    private final MessageService messageService;
    private final DurationService durationService;
    private final PlayTimeService playTimeService;

    @Inject
    public PlayTimeSetCommand(
            MessageService messageService,
            DurationService durationService,
            PlayTimeService playTimeService
    ) {
        this.messageService = messageService;
        this.durationService = durationService;
        this.playTimeService = playTimeService;
    }

    @Execute
    void setPlayTime(@Context CommandSender sender, @Arg PlayTimeUser target, @Arg Duration time) {
        playTimeService.setPlayTime(target, PlayTime.of(time));

        messageService.create()
                .viewer(sender)
                .notice(n -> n.playtimeMessages.playerPlayTimeUpdated())
                .placeholder("{PLAYER_NAME}", target.getName())
                .placeholder("{PLAYER_PLAYTIME}", durationService.format(time))
                .send();
    }
}
