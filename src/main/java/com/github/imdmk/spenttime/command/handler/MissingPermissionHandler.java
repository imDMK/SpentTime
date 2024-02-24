package com.github.imdmk.spenttime.command.handler;

import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import com.github.imdmk.spenttime.text.Formatter;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;

public class MissingPermissionHandler implements MissingPermissionsHandler<CommandSender> {

    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public MissingPermissionHandler(NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }


    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> resultHandlerChain) {
        Formatter formatter = new Formatter()
                .placeholder("{PERMISSIONS}", missingPermissions.getPermissions());

        this.notificationSender.send(invocation.sender(), this.notificationSettings.missingPermissions, formatter);
    }
}
