package com.github.imdmk.playtime.platform.litecommands.handler;

import com.github.imdmk.playtime.injector.annotations.lite.LiteHandler;
import com.github.imdmk.playtime.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@LiteHandler(value = CommandSender.class)
public final class MissingPermissionsHandlerImpl implements MissingPermissionsHandler<CommandSender> {

    private final MessageService messageService;

    public MissingPermissionsHandlerImpl(@NotNull MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions permissions, ResultHandlerChain<CommandSender> chain) {
        messageService.create()
                .viewer(invocation.sender())
                .notice(n -> n.commandPermissionMissing)
                .placeholder("{PERMISSIONS}", String.join(", ", permissions.getPermissions()))
                .send();
    }
}
