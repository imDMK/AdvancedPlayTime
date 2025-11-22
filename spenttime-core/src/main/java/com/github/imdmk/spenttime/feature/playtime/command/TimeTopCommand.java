package com.github.imdmk.spenttime.feature.playtime.command;

import com.github.imdmk.spenttime.feature.playtime.gui.PlaytimeTopGui;
import com.github.imdmk.spenttime.platform.gui.view.GuiOpener;
import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.shared.config.PluginConfig;
import com.github.imdmk.spenttime.shared.message.MessageService;
import com.github.imdmk.spenttime.user.UserService;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Command(name = "spenttime top")
@Permission("command.spenttime.top")
public final class TimeTopCommand {

    private static final int TOP_QUERY_LIMIT = 100;

    private final PluginLogger logger;
    private final PluginConfig config;
    private final MessageService messageService;
    private final UserService userService;
    private final GuiOpener guiOpener;

    @Inject
    public TimeTopCommand(
            @NotNull PluginLogger logger,
            @NotNull PluginConfig config,
            @NotNull MessageService messageService,
            @NotNull UserService userService,
            @NotNull GuiOpener guiOpener) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.config = Validator.notNull(config, "config cannot be null");
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
        this.userService = Validator.notNull(userService, "userService cannot be null");
        this.guiOpener = Validator.notNull(guiOpener, "guiOpener cannot be null");
    }

    @Execute
    void playtimeTop(@Context Player viewer) {
        userService.findTopBySpentTime(TOP_QUERY_LIMIT)
                .thenAccept(topUsers -> guiOpener.open(PlaytimeTopGui.class, viewer, topUsers))
                .exceptionally(e -> {
                    logger.error(e, "Failed to open PlaytimeTopGui for viewer=%s", viewer.getName());
                    this.messageService.send(viewer, n -> n.actionExecutionError);
                    return null;
                });
    }
}
