package com.github.imdmk.spenttime.command.handler;

import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

public class UsageHandler implements InvalidUsageHandler<CommandSender> {

    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public UsageHandler(NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation liteInvocation, Schematic schematic) {
        if (schematic.isOnlyFirst()) {
            Notification notification = Notification.builder()
                    .fromNotification(this.notificationSettings.invalidUsageNotification)
                    .placeholder("{USAGE}", schematic.first())
                    .build();

            this.notificationSender.send(sender, notification);
            return;
        }

        this.notificationSender.send(sender, this.notificationSettings.invalidUsageFirstNotification);

        for (String schema : schematic.getSchematics()) {
            Notification notification = Notification.builder()
                    .fromNotification(this.notificationSettings.invalidUsageListNotification)
                    .placeholder("{USAGE}", schema)
                    .build();

            this.notificationSender.send(sender, notification);
        }
    }
}
