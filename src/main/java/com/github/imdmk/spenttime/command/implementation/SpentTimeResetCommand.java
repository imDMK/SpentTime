package com.github.imdmk.spenttime.command.implementation;

import com.github.imdmk.spenttime.gui.implementation.ConfirmGui;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.text.Formatter;
import com.github.imdmk.spenttime.user.User;
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
    private final NotificationSettings notificationSettings;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final TaskScheduler taskScheduler;

    public SpentTimeResetCommand(Server server, NotificationSettings notificationSettings, UserRepository userRepository, NotificationSender notificationSender, TaskScheduler taskScheduler) {
        this.server = server;
        this.notificationSettings = notificationSettings;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.taskScheduler = taskScheduler;
    }

    @Execute(route = "reset", required = 1)
    void resetTime(CommandSender sender, @Arg @Name("target") User target) {
        if (sender instanceof Player player) {
            new ConfirmGui(this.taskScheduler)
                    .create(ComponentUtil.createItalic("<red>Reset " + target.getName() + " player spent time?"))
                    .afterConfirm(event -> {
                        player.closeInventory();

                        this.taskScheduler.runAsync(() -> this.resetSpentTime(sender, target));
                    })
                    .closeAfterCancel()
                    .open(player);
            return;
        }

        this.taskScheduler.runAsync(() -> this.resetSpentTime(sender, target));
    }

    @Execute(route = "reset-all")
    void resetTimeAll(CommandSender sender) {
        if (sender instanceof Player player) {
            new ConfirmGui(this.taskScheduler)
                    .create(ComponentUtil.createItalic("<red>Reset spent time of all users?"))
                    .afterConfirm(event -> {
                        player.closeInventory();

                        this.taskScheduler.runAsync(() -> this.resetGlobalSpentTime(sender));
                    })
                    .closeAfterCancel()
                    .open(player);
            return;
        }

        this.taskScheduler.runAsync(() -> this.resetGlobalSpentTime(sender));
    }

    private void resetSpentTime(CommandSender sender, User target) {
        target.setSpentTime(0L);
        this.userRepository.save(target);

        OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(target.getUuid());
        offlinePlayer.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);

        Formatter formatter = new Formatter()
                .placeholder("{PLAYER}", target.getName());

        this.notificationSender.send(sender, this.notificationSettings.targetResetSpentTimeNotification, formatter);
    }

    private void resetGlobalSpentTime(CommandSender sender) {
        this.userRepository.resetGlobalSpentTime();

        for (OfflinePlayer offlinePlayer : this.server.getOfflinePlayers()) {
            offlinePlayer.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
        }

        this.notificationSender.send(sender, this.notificationSettings.resetGlobalSpentTimeNotification);
    }
}
