package com.github.imdmk.playtime.core.feature.playtime.command;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.feature.playtime.repository.PlayTimeUserRepository;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.core.message.MessageService;
import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import com.github.imdmk.playtime.core.platform.playtime.PlayTimeAdapter;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@LiteCommand
@Command(name = "playtime reset-all")
@Permission(PlayTimeCommandPermissions.PLAYTIME_RESET)
public final class PlayTimeResetAllCommand {

    private final Server server;
    private final PluginLogger logger;
    private final MessageService messageService;
    private final PlayTimeAdapter adapter;
    private final PlayTimeUserRepository repository;
    private final TaskScheduler scheduler;

    public PlayTimeResetAllCommand(
            Server server,
            PluginLogger logger,
            MessageService messageService,
            PlayTimeAdapter adapter,
            PlayTimeUserRepository repository,
            TaskScheduler scheduler
    ) {
        this.server = server;
        this.logger = logger;
        this.messageService = messageService;
        this.adapter = adapter;
        this.repository = repository;
        this.scheduler = scheduler;
    }

    @Execute
    void resetAll(@Context CommandSender sender) {
        repository.resetAllPlayTimes()
                .thenAccept(v -> {
                    scheduler.runSync(this::resetOnlinePlayersPlayTime);
                    messageService.send(sender, n -> n.playtimeMessages.playersPlayTimeReset());
                })
                .exceptionally(e -> {
                    logger.error(e, "Failed to reset all playtimes");
                    messageService.send(sender, n -> n.actionExecutionError);
                    return null;
                });
    }

    private void resetOnlinePlayersPlayTime() {
        for (Player player : server.getOnlinePlayers()) {
            adapter.write(player, PlayTime.ZERO);
        }
    }
}
