package com.github.imdmk.spenttime.command;

import com.github.imdmk.spenttime.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import com.github.imdmk.spenttime.gui.ConfirmGui;
import com.github.imdmk.spenttime.gui.top.TopSpentTimeGui;
import com.github.imdmk.spenttime.gui.top.TopSpentTimePaginatedGui;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Route(name = "spent-time")
public class SpentTimeCommand {

    private final Server server;
    private final GuiConfiguration guiConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final UserRepository userRepository;
    private final UserManager userManager;
    private final NotificationSender notificationSender;
    private final TaskScheduler taskScheduler;
    private final TopSpentTimeGui topSpentTimeGui;
    private final TopSpentTimePaginatedGui topSpentTimePaginatedGui;

    public SpentTimeCommand(Server server, GuiConfiguration guiConfiguration, MessageConfiguration messageConfiguration, UserRepository userRepository, UserManager userManager, NotificationSender notificationSender, TaskScheduler taskScheduler, TopSpentTimeGui topSpentTimeGui, TopSpentTimePaginatedGui topSpentTimePaginatedGui) {
        this.server = server;
        this.guiConfiguration = guiConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.userRepository = userRepository;
        this.userManager = userManager;
        this.notificationSender = notificationSender;
        this.taskScheduler = taskScheduler;
        this.topSpentTimeGui = topSpentTimeGui;
        this.topSpentTimePaginatedGui = topSpentTimePaginatedGui;
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
        List<User> topUsers = this.userRepository.findUsersByOrderSpentTime(this.guiConfiguration.querySize);

        if (topUsers.isEmpty()) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.topSpentTimeIsEmpty);
            return;
        }

        if (this.guiConfiguration.enabled) {
            switch (this.guiConfiguration.guiType) {
                case BASIC -> this.topSpentTimeGui.open(player, topUsers, true);
                case PAGINATED -> this.topSpentTimePaginatedGui.open(player, topUsers, true);
                default -> throw new IllegalStateException("Unexpected gui type value: " + this.guiConfiguration.guiType);
            }
            return;
        }

        this.notificationSender.sendMessage(player, this.messageConfiguration.topSpentTimeListFirstNotification);

        AtomicInteger position = new AtomicInteger(1);

        for (User user : topUsers) {
            Notification notification = Notification.builder()
                    .fromNotification(this.messageConfiguration.topSpentTimeListNotification)
                    .placeholder("{POSITION}", position.getAndIncrement())
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

    @Execute(route = "reset-time-all")
    void resetTimeAll(CommandSender sender) {
        if (sender instanceof Player player) {
            new ConfirmGui(this.taskScheduler, "<red>Reset spent time of all users?")
                    .afterConfirm(event -> {
                        player.closeInventory();

                        this.taskScheduler.runAsync(this::resetTimeAll);
                        this.notificationSender.sendMessage(player, this.messageConfiguration.resetSpentTimeForAllUsersNotification);
                    })
                    .afterCancel(event -> player.closeInventory())
                    .open(player, false);
            return;
        }

        this.taskScheduler.runAsync(this::resetTimeAll);
        this.notificationSender.sendMessage(sender, this.messageConfiguration.resetSpentTimeForAllUsersNotification);
    }

    private void resetTimeAll() {
        this.userRepository.dropTable();
        this.userRepository.createTable();

        for (OfflinePlayer offlinePlayer : this.server.getOfflinePlayers()) {
            if (!offlinePlayer.hasPlayedBefore()) {
                return;
            }

            offlinePlayer.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
        }
    }
}
