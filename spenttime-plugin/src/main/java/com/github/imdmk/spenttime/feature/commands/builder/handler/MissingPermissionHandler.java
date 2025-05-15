package com.github.imdmk.spenttime.feature.commands.builder.handler;

import com.github.imdmk.spenttime.feature.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MissingPermissionHandler implements MissingPermissionsHandler<CommandSender> {

    private final MessageService messageService;

    public MissingPermissionHandler(@NotNull MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions permissions, ResultHandlerChain<CommandSender> resultHandlerChain) {
        this.messageService.create()
                .viewer(invocation.sender())
                .notice(notice -> notice.noPermission)
                .placeholder("{PERMISSIONS}", String.join(", ", permissions.getPermissions()))
                .send();
    }
}
