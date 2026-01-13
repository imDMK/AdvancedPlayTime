package com.github.imdmk.playtime.feature.playtime.top;

import com.github.imdmk.playtime.feature.playtime.top.gui.PlayTimeTopGui;
import com.github.imdmk.playtime.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.platform.gui.view.GuiOpener;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.message.MessageService;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@LiteCommand
@Command(name = "playtime top")
@Permission("command.playtime.top")
final class PlayTimeTopCommand {

    private final PluginLogger logger;
    private final MessageService messageService;
    private final GuiOpener guiOpener;
    private final PlayTimeTopService topService;

    @Inject
    PlayTimeTopCommand(
            @NotNull PluginLogger logger,
            @NotNull MessageService messageService,
            @NotNull GuiOpener guiOpener,
            @NotNull PlayTimeTopService topService
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
}
