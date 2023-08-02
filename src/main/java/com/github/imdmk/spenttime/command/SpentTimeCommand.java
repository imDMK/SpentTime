package com.github.imdmk.spenttime.command;

import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.gui.TopSpentTimeGui;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import com.github.imdmk.spenttime.util.PlayerUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.async.Async;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;

@Route(name = "spent-time")
public class SpentTimeCommand {

    private final PluginConfiguration pluginConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final UserRepository userRepository;
    private final UserManager userManager;
    private final NotificationSender notificationSender;
    private final TopSpentTimeGui topSpentTimeGui;

    public SpentTimeCommand(PluginConfiguration pluginConfiguration, MessageConfiguration messageConfiguration, UserRepository userRepository, UserManager userManager, NotificationSender notificationSender, TopSpentTimeGui topSpentTimeGui) {
        this.pluginConfiguration = pluginConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.userRepository = userRepository;
        this.userManager = userManager;
        this.notificationSender = notificationSender;
        this.topSpentTimeGui = topSpentTimeGui;
    }

    @Async
    @Execute(required = 0)
    void showSelfSpentTime(Player player) {
        Duration playerSpentTime = Duration.ofMillis(PlayerUtil.getSpentTime(player));

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.spentTimeNotification)
                .placeholder("{TIME}", DurationUtil.toHumanReadable(playerSpentTime))
                .build();

        this.notificationSender.sendMessage(player, notification);
    }

    @Async
    @Execute(required = 1)
    void showTargetSpentTime(CommandSender sender, @Arg @Name("target") Player target) {
        Duration targetSpentTime = Duration.ofMillis(PlayerUtil.getSpentTime(target));

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.targetSpentTimeNotification)
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{TIME}", DurationUtil.toHumanReadable(targetSpentTime))
                .build();

        this.notificationSender.sendMessage(sender, notification);
    }

    @Async
    @Execute(route = "top", required = 0)
    void showTopSpentTime(Player player) {
        List<User> topSpentTimeUsers = this.userRepository.findUsersByOrderSpentTime(10L);

        if (topSpentTimeUsers.isEmpty()) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.topSpentTimeIsEmpty);
            return;
        }

        if (this.pluginConfiguration.spentTimeGuiEnabled) {
            this.topSpentTimeGui.open(player, topSpentTimeUsers, true);
            return;
        }

        this.notificationSender.sendMessage(player, this.messageConfiguration.topSpentTimeListFirstNotification);

        for (User user : topSpentTimeUsers) {
            Notification notification = Notification.builder()
                    .fromNotification(this.messageConfiguration.topSpentTimeListNotification)
                    .placeholder("{PLAYER}", user.getName())
                    .placeholder("{TIME}", DurationUtil.toHumanReadable(user.getSpentTimeDuration()))
                    .build();

            this.notificationSender.sendMessage(player, notification);
        }
    }

    @Async
    @Execute(route = "reset-time", required = 1)
    void resetTime(CommandSender sender, @Arg @Name("target") Player player) {
        this.userManager.getOrFindUser(player.getUniqueId()).ifPresent(user -> {
            user.setSpentTime(0L);
            this.userRepository.save(user);
        });

        player.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
        player.saveData();

        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.targetResetTimeNotification)
                .placeholder("{PLAYER}", player.getName())
                .build();

        this.notificationSender.sendMessage(sender, notification);
    }
}
