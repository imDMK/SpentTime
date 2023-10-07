package com.github.imdmk.spenttime.command.handler;

import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import com.github.imdmk.spenttime.text.Formatter;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;

public class MissingPermissionHandler implements PermissionHandler<CommandSender> {

    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public MissingPermissionHandler(NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender commandSender, LiteInvocation liteInvocation, RequiredPermissions requiredPermissions) {
        Formatter formatter = new Formatter()
                .placeholder("{PERMISSIONS}", requiredPermissions.getPermissions());

        this.notificationSender.send(commandSender, this.notificationSettings.missingPermissions, formatter);
    }
}
