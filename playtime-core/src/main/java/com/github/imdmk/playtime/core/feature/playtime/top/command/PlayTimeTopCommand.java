package com.github.imdmk.playtime.core.feature.playtime.top.command;

import com.github.imdmk.playtime.core.feature.playtime.command.PlayTimeCommandPermissions;
import com.github.imdmk.playtime.core.feature.playtime.top.PlayTimeTopService;
import com.github.imdmk.playtime.core.feature.playtime.top.gui.PlayTimeTopGui;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.core.message.MessageService;
import com.github.imdmk.playtime.core.platform.gui.view.GuiOpener;
import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

@LiteCommand
@Command(name = "playtime top")
@Permission(PlayTimeCommandPermissions.PLAYTIME_TOP)
final class PlayTimeTopCommand {

    private final PluginLogger logger;
    private final MessageService messageService;
    private final GuiOpener guiOpener;
    private final PlayTimeTopService topService;

    @Inject
    PlayTimeTopCommand(
            PluginLogger logger,
            MessageService messageService,
            GuiOpener guiOpener,
            PlayTimeTopService topService
    ) {
        this.logger = logger;
        this.messageService = messageService;
        this.guiOpener = guiOpener;
        this.topService = topService;
    }

    @Execute
    void openGui(@Context Player viewer) {
        topService.getTop()
                .thenAccept(topUsers -> guiOpener.open(PlayTimeTopGui.class, viewer, topUsers))
                .exceptionally(e -> {
                    messageService.send(viewer, notice -> notice.actionExecutionError);
                    logger.error(e, "Failed to open top users");
                    return null;
                });
    }

    @Execute(name = "invalidate")
    @Permission(PlayTimeCommandPermissions.PLAYTIME_TOP_INVALIDATE)
    void invalidate(@Context CommandSender sender) {
        topService.invalidateCache();
        messageService.send(sender, notice -> notice.playtimeMessages.playTimeTopCacheInvalidated());
    }
}
