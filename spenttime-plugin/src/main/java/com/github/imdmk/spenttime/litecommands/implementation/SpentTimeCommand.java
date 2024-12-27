package com.github.imdmk.spenttime.litecommands.implementation;

import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationFormatter;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@Command(name = "spenttime")
public class SpentTimeCommand {

    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;
    private final BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService;

    public SpentTimeCommand(NotificationSettings notificationSettings, NotificationSender notificationSender, BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService) {
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
        this.bukkitPlayerSpentTimeService = bukkitPlayerSpentTimeService;
    }

    @Execute
    @Permission("command.spenttime")
    void showSpentTime(@Context Player player) {
        String playerSpentTime = DurationUtil.toHumanReadable(this.bukkitPlayerSpentTimeService.getSpentTime(player));

        Notification notification = new NotificationFormatter()
                .notification(this.notificationSettings.playerSpentTime)
                .placeholder("{TIME}", playerSpentTime)
                .build();

        this.notificationSender.send(player, notification);
    }

    @Async
    @Execute
    @Permission("command.spenttime.target")
    void showTarget(@Context CommandSender sender, @Arg User target) {
        String targetName = target.getName();
        String targetSpentTime = DurationUtil.toHumanReadable(this.updateSpentTime(target));

        Notification notification = new NotificationFormatter()
                .notification(this.notificationSettings.targetSpentTime)
                .placeholder("{PLAYER}", targetName)
                .placeholder("{TIME}", targetSpentTime)
                .build();

        this.notificationSender.send(sender, notification);
    }

    private Duration updateSpentTime(User target) {
        Duration playerSpentTime = this.bukkitPlayerSpentTimeService.getSpentTime(target.getUuid());
        Duration userSpentTime = target.getSpentTimeDuration();

        if (!playerSpentTime.equals(userSpentTime)) {
            target.setSpentTime(playerSpentTime);
        }

        return playerSpentTime;
    }
}
