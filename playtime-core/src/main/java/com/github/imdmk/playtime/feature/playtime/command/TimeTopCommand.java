package com.github.imdmk.playtime.feature.playtime.command;

import com.github.imdmk.playtime.feature.playtime.gui.PlayTimeTopGui;
import com.github.imdmk.playtime.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.message.MessageService;
import com.github.imdmk.playtime.platform.gui.view.GuiOpener;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.user.UserService;
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
public final class TimeTopCommand {

    private static final int TOP_QUERY_LIMIT = 100;

    private final PluginLogger logger;
    private final MessageService messageService;
    private final UserService userService;
    private final GuiOpener guiOpener;

    @Inject
    public TimeTopCommand(
            @NotNull PluginLogger logger,
            @NotNull MessageService messageService,
            @NotNull UserService userService,
            @NotNull GuiOpener guiOpener
    ) {
        this.logger = logger;
        this.messageService = messageService;
        this.userService = userService;
        this.guiOpener = guiOpener;
    }

    @Execute
    void playtimeTop(@Context Player viewer) {
        userService.findTopByPlayTime(TOP_QUERY_LIMIT)
                .thenAccept(topUsers -> guiOpener.open(PlayTimeTopGui.class, viewer, topUsers))
                .exceptionally(e -> {
                    logger.error(e, "Failed to open PlaytimeTopGui for viewer=%s", viewer.getName());
                    messageService.send(viewer, n -> n.actionExecutionError);
                    return null;
                });
    }
}
