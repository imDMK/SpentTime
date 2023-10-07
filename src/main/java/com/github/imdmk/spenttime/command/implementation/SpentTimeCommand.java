package com.github.imdmk.spenttime.command.implementation;

import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.text.Formatter;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.util.DurationUtil;
import com.github.imdmk.spenttime.util.PlayerUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@Route(name = "spenttime")
public class SpentTimeCommand {

    private final NotificationSettings notificationSettings;
    private final NotificationSender notificationSender;

    public SpentTimeCommand(NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Execute(required = 0)
    void showSpentTime(Player player) {
        Duration playerSpentTime = PlayerUtil.getSpentTimeDuration(player);

        Formatter formatter = new Formatter()
                .placeholder("{TIME}", DurationUtil.toHumanReadable(playerSpentTime));

        this.notificationSender.send(player, this.notificationSettings.spentTimeNotification, formatter);
    }

    @Execute(required = 1)
    void showSpentTimeTarget(CommandSender sender, @Arg User target) {
        Duration targetSpentTime = PlayerUtil.getSpentTimeDuration(target);

        Formatter formatter = new Formatter()
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{TIME}", DurationUtil.toHumanReadable(targetSpentTime));

        this.notificationSender.send(sender, this.notificationSettings.targetSpentTimeNotification, formatter);
    }
}
