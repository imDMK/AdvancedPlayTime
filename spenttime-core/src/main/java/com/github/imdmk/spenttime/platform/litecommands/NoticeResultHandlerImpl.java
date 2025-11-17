package com.github.imdmk.spenttime.platform.litecommands;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.shared.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class NoticeResultHandlerImpl implements ResultHandler<CommandSender, Notice> {

    private final MessageService messageService;

    public NoticeResultHandlerImpl(@NotNull MessageService messageService) {
        this.messageService = Validator.notNull(messageService, "messageService cannot be null");
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, Notice notice, ResultHandlerChain<CommandSender> chain) {
        messageService.send(invocation.sender(), n -> notice);
    }
}
