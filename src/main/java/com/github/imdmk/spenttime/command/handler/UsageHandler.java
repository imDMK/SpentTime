package com.github.imdmk.spenttime.command.handler;

import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import com.github.imdmk.spenttime.text.Formatter;
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
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> invalidUsage, ResultHandlerChain<CommandSender> resultHandlerChain) {
        Schematic schematic = invalidUsage.getSchematic();

        if (schematic.isOnlyFirst()) {
            Formatter formatter = new Formatter()
                    .placeholder("{USAGE}", schematic.first());

            this.notificationSender.send(invocation.sender(), this.notificationSettings.invalidUsage, formatter);
            return;
        }

        this.notificationSender.send(invocation.sender(), this.notificationSettings.invalidUsageFirst);

        for (String schema : schematic.all()) {
            Formatter formatter = new Formatter()
                    .placeholder("{USAGE}", schema);

            this.notificationSender.send(invocation.sender(), this.notificationSettings.invalidUsageList, formatter);
        }
    }
}
