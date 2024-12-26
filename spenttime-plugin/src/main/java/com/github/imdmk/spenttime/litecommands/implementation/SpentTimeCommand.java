package com.github.imdmk.spenttime.litecommands.implementation;

import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationFormatter;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    @Execute
    @Permission("command.spenttime.target")
    void showTarget(@Context CommandSender sender, @Arg User target) {
        String userSpentTime = DurationUtil.toHumanReadable(target.getSpentTimeDuration());

        Notification notification = new NotificationFormatter()
                .notification(this.notificationSettings.targetSpentTime)
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{TIME}", userSpentTime)
                .build();

        this.notificationSender.send(sender, notification);
    }
}
