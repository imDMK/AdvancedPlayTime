package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.core.platform.identity.IdentityService;
import com.github.imdmk.playtime.core.shared.message.MessageService;
import com.github.imdmk.playtime.core.shared.time.Durations;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;
import java.util.UUID;

@LiteCommand
@Command(name = "playtime")
final class PlayTimeCommand {

    private final MessageService messageService;
    private final IdentityService identityService;
    private final PlayTimeUserService userService;
    private final PlayTimeQueryService playTimeQueryService;

    @Inject
    PlayTimeCommand(
            MessageService messageService,
            IdentityService identityService,
            PlayTimeUserService userService,
            PlayTimeQueryService playTimeQueryService
    ) {
        this.messageService = messageService;
        this.identityService = identityService;
        this.userService = userService;
        this.playTimeQueryService = playTimeQueryService;
    }

    @Execute
    void playTime(@Context Player player) {
        playTimeQueryService.getCurrentPlayTime(player.getUniqueId())
                .thenAccept(playTime -> messageService.create()
                        .viewer(player)
                        .notice(n -> n.playtimeMessages.playerPlayTimeSelf())
                        .placeholder("{PLAYER_PLAYTIME}", Durations.format(playTime.toDuration()))
                        .send())
                .exceptionally(e -> {
                    messageService.send(player, notice -> notice.actionExecutionError);
                    return null;
                });
    }

    @Execute
    void playTimeOther(@Context CommandSender sender, @Arg UUID playerId) {
        playTimeQueryService.getCurrentPlayTime(playerId)
                .thenAccept(playTime -> messageService.create()
                        .viewer(sender)
                        .notice(n -> n.playtimeMessages.playerPlayTimeTarget())
                        .placeholder("{PLAYER_NAME}", identityService.resolvePlayerName(playerId))
                        .placeholder("{PLAYER_PLAYTIME}", Durations.format(playTime.toDuration()))
                        .send())
                .exceptionally(e -> {
                    messageService.send(sender, notice -> notice.actionExecutionError);
                    return null;
                });
    }

    @Execute
    void setPlayTime(@Context CommandSender sender, @Arg UUID playerId, @Arg Duration time) {
         PlayTime playTime = PlayTime.of(time);

        userService.setPlayTime(playerId, playTime)
                .thenAccept(v -> messageService.create()
                        .viewer(sender)
                        .notice(n -> n.playtimeMessages.playerPlayTimeUpdated())
                        .placeholder("{PLAYER_PLAYTIME}", Durations.format(time))
                        .send())
                .exceptionally(e -> {
                    messageService.send(sender, notice -> notice.actionExecutionError);
                    return null;
                });
    }
}
