package com.github.imdmk.spenttime.litecommands.handler;

import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationFormatter;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MissingPermissionHandler implements MissingPermissionsHandler<CommandSender> {

    private final NotificationSender notificationSender;
    private final NotificationSettings notificationSettings;

    public MissingPermissionHandler(NotificationSender notificationSender, NotificationSettings notificationSettings) {
        this.notificationSender = notificationSender;
        this.notificationSettings = notificationSettings;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> resultHandlerChain) {
        CommandSender sender = invocation.sender();
        List<String> permissions = missingPermissions.getPermissions();

        Notification notification = new NotificationFormatter()
                .notification(this.notificationSettings.missingPermissions)
                .placeholder("{PERMISSIONS}", permissions)
                .build();

        this.notificationSender.send(sender, notification);
    }
}
