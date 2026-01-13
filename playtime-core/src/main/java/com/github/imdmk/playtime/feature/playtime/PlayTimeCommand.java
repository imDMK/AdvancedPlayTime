package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.platform.identity.IdentityService;
import com.github.imdmk.playtime.shared.message.MessageService;
import com.github.imdmk.playtime.shared.time.Durations;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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
            @NotNull MessageService messageService,
            @NotNull IdentityService identityService,
            @NotNull PlayTimeUserService userService,
            @NotNull PlayTimeQueryService playTimeQueryService
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
        final PlayTime playTime = PlayTime.of(time);

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
