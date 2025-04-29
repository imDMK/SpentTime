package com.github.imdmk.spenttime.litecommands.handler;

import com.github.imdmk.spenttime.message.MessageService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MissingPermissionHandler implements MissingPermissionsHandler<CommandSender> {

    private final MessageService messageService;

    public MissingPermissionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> resultHandlerChain) {
        CommandSender sender = invocation.sender();
        List<String> permissions = missingPermissions.getPermissions();

        this.messageService.create()
                .viewer(sender)
                .notice(notice -> notice.missingPermissions)
                .placeholder("{PERMISSIONS}", String.join(", ", permissions))
                .send();
    }
}
