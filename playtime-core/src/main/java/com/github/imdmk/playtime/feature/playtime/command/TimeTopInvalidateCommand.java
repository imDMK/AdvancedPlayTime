package com.github.imdmk.playtime.feature.playtime.command;

import com.github.imdmk.playtime.message.MessageService;
import com.github.imdmk.playtime.user.top.TopUsersCache;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Command(name = "playtime top invalidate")
@Permission("command.playtime.top.invalidate")
public final class TimeTopInvalidateCommand {

    private final MessageService messageService;
    private final TopUsersCache topUsersCache;

    @Inject
    public TimeTopInvalidateCommand(
            @NotNull MessageService messageService,
            @NotNull TopUsersCache topUsersCache
    ) {
        this.messageService = messageService;
        this.topUsersCache = topUsersCache;
    }

    @Execute
    void invalidateCache(@Context CommandSender sender) {
        topUsersCache.invalidateAll();
        messageService.send(sender, n -> n.playtimeMessages.topUsersCacheInvalidated());
    }
}
