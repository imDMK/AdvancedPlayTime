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

@LiteCommand
@Command(name = "playtime")
final class PlayTimeCommand {

    private final MessageService messageService;
    private final DurationService durationService;
    private final PlayTimeService playTimeService;

    @Inject
    PlayTimeCommand(
            MessageService messageService,
            DurationService durationService,
            PlayTimeService playTimeService
    ) {
        this.messageService = messageService;
        this.durationService = durationService;
        this.playTimeService = playTimeService;
    }

    @Execute
    @Permission(PlayTimeCommandPermissions.PLAYTIME)
    void playTime(@Context PlayTimeUser user) {
        PlayTime playTime = playTimeService.getCurrentPlayTime(user);

        messageService.create()
                .player(user.getUuid())
                .notice(n -> n.playtimeMessages.playerPlayTimeSelf())
                .placeholder("{PLAYER_PLAYTIME}", durationService.format(playTime.toDuration()))
                .send();
    }

    @Execute
    @Permission(PlayTimeCommandPermissions.PLAYTIME_TARGET)
    void playTimeTarget(@Context CommandSender sender, @Arg PlayTimeUser target) {
        PlayTime playTime = playTimeService.getCurrentPlayTime(target);

        messageService.create()
                .viewer(sender)
                .notice(n -> n.playtimeMessages.playerPlayTimeTarget())
                .placeholder("{PLAYER_NAME}", target.getName())
                .placeholder("{PLAYER_PLAYTIME}", durationService.format(playTime.toDuration()))
                .send();
    }
}
