package com.github.imdmk.spenttime.command;

import com.github.imdmk.spenttime.configuration.implementation.MessageConfiguration;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.util.DurationUtil;
import com.github.imdmk.spenttime.util.PlayerUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@Route(name = "spenttime")
public class SpentTimeCommand {

    private final Server server;
    private final MessageConfiguration messageConfiguration;
    private final NotificationSender notificationSender;

    public SpentTimeCommand(Server server, MessageConfiguration messageConfiguration, NotificationSender notificationSender) {
        this.server = server;
        this.messageConfiguration = messageConfiguration;
        this.notificationSender = notificationSender;
    }

    @Execute(required = 0)
    void showSelfSpentTime(Player player) {
        Duration playerSpentTime = PlayerUtil.getSpentTimeDuration(player);

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.spentTimeNotification)
                .placeholder("{TIME}", DurationUtil.toHumanReadable(playerSpentTime))
                .build();

        this.notificationSender.send(player, notification);
    }

    @Execute(required = 1)
    void showTargetSpentTime(CommandSender sender, @Arg User target) {
        OfflinePlayer targetPlayer = this.server.getOfflinePlayer(target.getUuid());
        Duration targetSpentTime = PlayerUtil.getSpentTimeDuration(targetPlayer);

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.targetSpentTimeNotification)
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{TIME}", DurationUtil.toHumanReadable(targetSpentTime))
                .build();

        this.notificationSender.send(sender, notification);
    }
}
