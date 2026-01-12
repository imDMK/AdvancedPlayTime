package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.injector.annotations.lite.LiteCommand;
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
    private final PlayTimeUserService userService;

    @Inject
    PlayTimeCommand(@NotNull MessageService messageService, @NotNull PlayTimeUserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Execute
    void playTime(@Context Player player) {
        userService.getPlayTime(player.getUniqueId())
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
        userService.getPlayTime(playerId)
                .thenAccept(playTime -> messageService.create()
                        .viewer(sender)
                        .notice(n -> n.playtimeMessages.playerPlayTimeTarget())
                        .placeholder("{PLAYER_PLAYTIME}", Durations.format(playTime.toDuration()))
                        .send())
                .exceptionally(e -> {
                    messageService.send(sender, notice -> notice.actionExecutionError);
                    return null;
                });
    }

    @Execute
    void setPlayTime(@Context CommandSender sender, @Arg UUID playerId, @Arg Duration duration) {
        final PlayTime playtime = PlayTime.of(duration);

        userService.setPlayTime(playerId, playtime)
                .thenAccept(v -> messageService.create()
                        .viewer(sender)
                        .notice(n -> n.playtimeMessages.playerPlayTimeUpdated())
                        .placeholder("{PLAYER_PLAYTIME}", Durations.format(duration))
                        .send())
                .exceptionally(e -> {
                    messageService.send(sender, notice -> notice.actionExecutionError);
                    return null;
                });
    }
}
