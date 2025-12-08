package com.github.imdmk.playtime.feature.playtime.command;

import com.github.imdmk.playtime.feature.playtime.gui.PlayTimeTopGui;
import com.github.imdmk.playtime.message.MessageService;
import com.github.imdmk.playtime.platform.gui.view.GuiOpener;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.validate.Validator;
import com.github.imdmk.playtime.user.UserService;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

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
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
        this.guiOpener = Validator.notNull(guiOpener, "guiOpener cannot be null");
    }

    @Execute
    void playtimeTop(@Context Player viewer) {
        userService.findTopByPlayTime(TOP_QUERY_LIMIT)
                .thenAccept(topUsers -> guiOpener.open(PlayTimeTopGui.class, viewer, topUsers))
                .exceptionally(e -> {
                    logger.error(e, "Failed to open PlaytimeTopGui for viewer=%s", viewer.getName());
                    this.messageService.send(viewer, n -> n.actionExecutionError);
                    return null;
                });
    }
}
