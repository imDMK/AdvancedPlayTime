package com.github.imdmk.playtime.core.platform.litecommands.handler;

import com.github.imdmk.playtime.core.injector.annotations.lite.LiteHandler;
import com.github.imdmk.playtime.core.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

@LiteHandler(value = CommandSender.class)
final class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSender> {

    private final MessageService messageService;

    InvalidUsageHandlerImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = invocation.sender();
        Schematic schematic = result.getSchematic();

        if (schematic.isOnlyFirst()) {
            messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.commandUsageInvalid)
                    .placeholder("{USAGE}", schematic.first())
                    .send();
            return;
        }

        messageService.create()
                .viewer(sender)
                .notice(notice -> notice.commandUsageHeader)
                .send();

        for (String scheme : schematic.all()) {
            messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.commandUsageEntry)
                    .placeholder("{USAGE}", scheme)
                    .send();
        }
    }
}
