package com.github.imdmk.playtime.feature.reload;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.shared.config.ConfigManager;
import com.github.imdmk.playtime.shared.message.MessageService;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Command(name = "playtime reload")
@Permission("command.playtime.reload")
public final class ReloadCommand {

    private final PluginLogger logger;
    private final ConfigManager configManager;
    private final MessageService messageService;

    @Inject
    public ReloadCommand(@NotNull PluginLogger logger, @NotNull ConfigManager configManager, @NotNull MessageService messageService) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.configManager = Validator.notNull(configManager, "configManager cannot be null");
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
    }

    @Execute
    void reload(@Context CommandSender sender) {
        configManager.loadAll()
                .thenRun(() -> messageService.send(sender, n -> n.reloadMessages.configReloadedSuccess()))
                .exceptionally(e -> {
                    logger.error(e, "Failed to reload plugin configuration files");
                    messageService.send(sender, n -> n.reloadMessages.configReloadFailed());
                    return null;
                });
    }
}
