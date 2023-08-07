package com.github.imdmk.spenttime.command;

import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.util.DurationUtil;
import com.github.imdmk.spenttime.util.PlayerUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.async.Async;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@Route(name = "spent-time")
public class SpentTimeCommand {

    private final MessageConfiguration messageConfiguration;
    private final NotificationSender notificationSender;

    public SpentTimeCommand(MessageConfiguration messageConfiguration, NotificationSender notificationSender) {
        this.messageConfiguration = messageConfiguration;
        this.notificationSender = notificationSender;
    }

    @Async
    @Execute(required = 0)
    void showSelfSpentTime(Player player) {
        Duration playerSpentTime = PlayerUtil.getSpentTimeDuration(player);

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.spentTimeNotification)
                .placeholder("{TIME}", DurationUtil.toHumanReadable(playerSpentTime))
                .build();

        this.notificationSender.sendMessage(player, notification);
    }

    @Async
    @Execute(required = 1)
    void showTargetSpentTime(CommandSender sender, @Arg @Name("target") Player target) {
        Duration targetSpentTime = PlayerUtil.getSpentTimeDuration(target);

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.targetSpentTimeNotification)
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{TIME}", DurationUtil.toHumanReadable(targetSpentTime))
                .build();

        this.notificationSender.sendMessage(sender, notification);
    }
}
