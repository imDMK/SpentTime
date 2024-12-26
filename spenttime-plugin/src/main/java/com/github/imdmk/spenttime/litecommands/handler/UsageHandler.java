package com.github.imdmk.spenttime.litecommands.handler;

import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationFormatter;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
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
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = invocation.sender();
        Schematic schematic = result.getSchematic();

        if (schematic.isOnlyFirst()) {
            Notification notification = new NotificationFormatter()
                    .notification(this.notificationSettings.invalidUsage)
                    .placeholder("{USAGE}", schematic.first())
                    .build();

            this.notificationSender.send(sender, notification);
            return;
        }

        this.notificationSender.send(sender, this.notificationSettings.invalidUsageFirst);

        for (String scheme : schematic.all()) {
            Notification notification = new NotificationFormatter()
                    .notification(this.notificationSettings.invalidUsageList)
                    .placeholder("{USAGE}", scheme)
                    .build();

            this.notificationSender.send(sender, notification);
        }
    }
}
