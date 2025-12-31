package com.github.imdmk.playtime.platform.litecommands;

import com.github.imdmk.playtime.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSender> {

    private final MessageService messageService;

    public InvalidUsageHandlerImpl(@NotNull MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        final CommandSender sender = invocation.sender();
        final Schematic schematic = result.getSchematic();

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

        for (final String scheme : schematic.all()) {
            messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.commandUsageEntry)
                    .placeholder("{USAGE}", scheme)
                    .send();
        }
    }
}
