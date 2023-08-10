package com.github.imdmk.spenttime.command;

import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import com.github.imdmk.spenttime.gui.implementation.ConfirmGui;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.UserManager;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Route(name = "spenttime")
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

    @Execute(route = "reset", required = 1)
    void resetTime(CommandSender sender, @Arg @Name("target") Player target) {
        if (sender instanceof Player player) {
            new ConfirmGui(this.taskScheduler, ComponentUtil.createItalic("<red>Reset " + target.getName() + " player spent time?"))
                    .afterConfirm(event -> {
                        player.closeInventory();

                        this.taskScheduler.runAsync(() -> this.resetSpentTime(target));
                        this.sendTargetResetNotification(sender, target);
                    })
                    .afterCancel(event -> player.closeInventory())
                    .open(player);
            return;
        }

        this.taskScheduler.runAsync(() -> this.resetSpentTime(target));
        this.sendTargetResetNotification(sender, target);
    }

    @Execute(route = "reset-all")
    void resetTimeAll(CommandSender sender) {
        if (sender instanceof Player player) {
            new ConfirmGui(this.taskScheduler, ComponentUtil.createItalic("<red>Reset spent time of all users?"))
                    .afterConfirm(event -> {
                        player.closeInventory();

                        this.taskScheduler.runAsync(this::resetGlobalSpentTime);
                        this.notificationSender.sendMessage(player, this.messageConfiguration.resetSpentTimeForAllUsersNotification);
                    })
                    .afterCancel(event -> player.closeInventory())
                    .open(player);
            return;
        }

        this.taskScheduler.runAsync(this::resetGlobalSpentTime);
        this.notificationSender.sendMessage(sender, this.messageConfiguration.resetSpentTimeForAllUsersNotification);
    }

    private void resetSpentTime(Player player) {
        this.userManager.getOrFindUser(player.getUniqueId())
                .ifPresent(user -> {
                    user.setSpentTime(0L);
                    this.userRepository.save(user);
                });

        player.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
        player.saveData();
    }

    private void resetGlobalSpentTime() {
        this.userRepository.resetGlobalSpentTime();

        for (OfflinePlayer offlinePlayer : this.server.getOfflinePlayers()) {
            offlinePlayer.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
        }
    }

    private void sendTargetResetNotification(CommandSender sender, Player target) {
        Notification notification = Notification.builder()
                .fromNotification(this.messageConfiguration.targetResetTimeNotification)
                .placeholder("{PLAYER}", target.getName())
                .build();

        this.notificationSender.sendMessage(sender, notification);
    }
}
