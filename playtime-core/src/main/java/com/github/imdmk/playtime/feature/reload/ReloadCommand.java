package com.github.imdmk.playtime.feature.reload;

import com.github.imdmk.playtime.config.ConfigManager;
import com.github.imdmk.playtime.message.MessageService;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.shared.validate.Validator;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import eu.okaeri.configs.exception.OkaeriException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Command(name = "playtime reload")
@Permission("command.playtime.reload")
public final class ReloadCommand {

    private final PluginLogger logger;
    private final ConfigManager configManager;
    private final TaskScheduler taskScheduler;
    private final MessageService messageService;

    @Inject
    public ReloadCommand(
            @NotNull PluginLogger logger,
            @NotNull ConfigManager configManager,
            @NotNull TaskScheduler taskScheduler,
            @NotNull MessageService messageService
    ) {
        this.logger = Validator.notNull(logger, "logger");
        this.configManager = Validator.notNull(configManager, "configManager");
        this.taskScheduler = Validator.notNull(taskScheduler, "taskScheduler");
        this.messageService = Validator.notNull(messageService, "messageService");
    }

    @Execute
    void reload(@Context CommandSender sender) {
        taskScheduler.runAsync(() -> {
            try {
                configManager.loadAll();
                messageService.send(sender, n -> n.reloadMessages.configReloadedSuccess());
            } catch (OkaeriException e) {
                logger.error(e, "Failed to reload plugin configuration files");
                messageService.send(sender, n -> n.reloadMessages.configReloadFailed());
            }
        });
    }
}
