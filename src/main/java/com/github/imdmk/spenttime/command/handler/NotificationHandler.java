package com.github.imdmk.spenttime.command.handler;

import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.Handler;
import org.bukkit.command.CommandSender;

public class NotificationHandler implements Handler<CommandSender, Notification> {

    private final NotificationSender notificationSender;

    public NotificationHandler(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @Override
    public void handle(CommandSender commandSender, LiteInvocation liteInvocation, Notification notification) {
        this.notificationSender.sendMessage(commandSender, notification);
    }
}
