package com.github.imdmk.spenttime.infrastructure.message;

import com.eternalcode.multification.notice.Notice;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MessageResultHandler implements ResultHandler<CommandSender, Notice> {

    private final MessageService messageService;

    public MessageResultHandler(@NotNull MessageService messageService) {
        this.messageService = Objects.requireNonNull(messageService, "messageService cannot be null");
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, Notice result, ResultHandlerChain<CommandSender> chain) {
        this.messageService.create()
                .viewer(invocation.sender())
                .notice(result)
                .send();
    }
}

