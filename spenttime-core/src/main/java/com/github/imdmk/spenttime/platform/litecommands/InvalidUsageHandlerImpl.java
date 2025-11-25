package com.github.imdmk.spenttime.platform.litecommands;

import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.shared.message.MessageService;
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
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = invocation.sender();
        Schematic schematic = result.getSchematic();

        if (schematic.isOnlyFirst()) {
            this.messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.commandUsageInvalid)
                    .placeholder("{USAGE}", schematic.first())
                    .send();
            return;
        }

        this.messageService.create()
                .viewer(sender)
                .notice(notice -> notice.commandUsageHeader)
                .send();

        for (String scheme : schematic.all()) {
            this.messageService.create()
                    .viewer(sender)
                    .notice(notice -> notice.commandUsageEntry)
                    .placeholder("{USAGE}", scheme)
                    .send();
        }
    }
}
