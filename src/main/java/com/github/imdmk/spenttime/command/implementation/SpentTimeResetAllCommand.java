package com.github.imdmk.spenttime.command.implementation;

import com.github.imdmk.spenttime.gui.implementation.ConfirmGui;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Route(name = "spenttime reset-all")
@Permission("command.spenttime.reset.all")
public class SpentTimeResetAllCommand {

    private final Server server;
    private final NotificationSettings notificationSettings;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final TaskScheduler taskScheduler;

    public SpentTimeResetAllCommand(Server server, NotificationSettings notificationSettings, UserRepository userRepository, NotificationSender notificationSender, TaskScheduler taskScheduler) {
        this.server = server;
        this.notificationSettings = notificationSettings;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.taskScheduler = taskScheduler;
    }

    @Execute
    void resetGlobal(CommandSender sender) {
        if (sender instanceof Player player) {
            this.showGui(player);
            return;
        }

        this.taskScheduler.runAsync(this::resetGlobalSpentTime);

        this.notificationSender.send(sender, this.notificationSettings.globalSpentTimeHasBeenReset);
    }

    private void resetGlobalSpentTime() {
        this.userRepository.resetGlobalSpentTime();

        for (OfflinePlayer offlinePlayer : this.server.getOfflinePlayers()) {
            offlinePlayer.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
        }
    }

    private void showGui(Player player) {
        new ConfirmGui(this.taskScheduler)
                .create(ComponentUtil.createItalic("<red>Reset spent time of all users?"))
                .afterConfirm(event -> {
                    player.closeInventory();

                    this.taskScheduler.runAsync(this::resetGlobalSpentTime);

                    this.notificationSender.send(player, this.notificationSettings.globalSpentTimeHasBeenReset);
                })
                .closeAfterCancel()
                .open(player);
    }
}
