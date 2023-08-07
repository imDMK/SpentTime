package com.github.imdmk.spenttime.command;

import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import com.github.imdmk.spenttime.gui.ConfirmGui;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.UserManager;
import com.github.imdmk.spenttime.user.repository.UserRepository;
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

@Route(name = "spent-time")
public class SpentTimeResetCommand {

    private final Server server;
    private final MessageConfiguration messageConfiguration;
    private final UserRepository userRepository;
    private final UserManager userManager;
    private final NotificationSender notificationSender;
    private final TaskScheduler taskScheduler;

    public SpentTimeResetCommand(Server server, MessageConfiguration messageConfiguration, UserRepository userRepository, UserManager userManager, NotificationSender notificationSender, TaskScheduler taskScheduler) {
        this.server = server;
        this.messageConfiguration = messageConfiguration;
        this.userRepository = userRepository;
        this.userManager = userManager;
        this.notificationSender = notificationSender;
        this.taskScheduler = taskScheduler;
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
