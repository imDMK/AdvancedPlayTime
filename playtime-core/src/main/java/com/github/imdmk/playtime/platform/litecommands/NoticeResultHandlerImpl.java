package com.github.imdmk.playtime.platform.litecommands;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.playtime.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class NoticeResultHandlerImpl implements ResultHandler<CommandSender, Notice> {

    private final MessageService messageService;

    public NoticeResultHandlerImpl(@NotNull MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, Notice notice, ResultHandlerChain<CommandSender> chain) {
        messageService.create()
                .viewer(invocation.sender())
                .notice(n -> notice)
                .send();
    }
}
