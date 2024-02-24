package com.github.imdmk.spenttime.command.implementation;

import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import com.github.imdmk.spenttime.text.Formatter;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.util.DurationUtil;
import com.github.imdmk.spenttime.util.PlayerUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
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

    public SpentTimeCommand(NotificationSettings notificationSettings, NotificationSender notificationSender) {
        this.notificationSettings = notificationSettings;
        this.notificationSender = notificationSender;
    }

    @Execute
    @Permission("command.spenttime")
    void show(@Context Player player) {
        Duration playerSpentTime = PlayerUtil.getSpentTime(player);

        Formatter formatter = new Formatter()
                .placeholder("{TIME}", DurationUtil.toHumanReadable(playerSpentTime));

        this.notificationSender.send(player, this.notificationSettings.playerSpentTime, formatter);
    }

    @Execute
    @Permission("command.spenttime.target")
    void showTarget(@Context CommandSender sender, @Arg User target) {
        Formatter formatter = new Formatter()
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{TIME}", DurationUtil.toHumanReadable(target.getSpentTimeDuration()));

        this.notificationSender.send(sender, this.notificationSettings.targetSpentTime, formatter);
    }
}
