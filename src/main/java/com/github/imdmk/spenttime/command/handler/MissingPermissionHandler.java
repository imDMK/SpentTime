package com.github.imdmk.spenttime.command.handler;

import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;

public class MissingPermissionHandler implements PermissionHandler<CommandSender> {

    private final MessageConfiguration messageConfiguration;
    private final NotificationSender notificationSender;

    public MissingPermissionHandler(MessageConfiguration messageConfiguration, NotificationSender notificationSender) {
        this.messageConfiguration = messageConfiguration;
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender commandSender, LiteInvocation liteInvocation, RequiredPermissions requiredPermissions) {
        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.missingPermissionsNotification)
                .placeholder("{PERMISSIONS}", requiredPermissions.getPermissions())
                .build();

        this.notificationSender.sendMessage(commandSender, notification);
    }
}
