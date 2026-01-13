package com.github.imdmk.playtime.feature.reload;

import com.github.imdmk.playtime.config.ConfigAccessException;
import com.github.imdmk.playtime.config.ConfigService;
import com.github.imdmk.playtime.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.shared.message.MessageService;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@LiteCommand
@Command(name = "playtime reload")
@Permission("command.playtime.reload")
final class ReloadCommand {

    private final PluginLogger logger;
    private final ConfigService configService;
    private final TaskScheduler taskScheduler;
    private final MessageService messageService;

    @Inject
    ReloadCommand(
            @NotNull PluginLogger logger,
            @NotNull ConfigService configService,
            @NotNull TaskScheduler taskScheduler,
            @NotNull MessageService messageService
    ) {
        this.logger = logger;
        this.configService = configService;
        this.taskScheduler = taskScheduler;
        this.messageService = messageService;
    }

    @Execute
    void reload(@Context CommandSender sender) {
        taskScheduler.runAsync(() -> {
            try {
                configService.loadAll();
                messageService.send(sender, n -> n.reloadMessages.configReloadedSuccess());
            } catch (ConfigAccessException e) {
                logger.error(e, "Failed to reload plugin configuration files");
                messageService.send(sender, n -> n.reloadMessages.configReloadFailed());
            }
        });
    }
}
