package com.github.imdmk.spenttime.litecommands.implementation;

import com.github.imdmk.spenttime.gui.ConfirmGui;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "spenttime reset-all")
@Permission("command.spenttime.reset.all")
public class SpentTimeResetAllCommand {

    private final Server server;
    private final NotificationSettings notificationSettings;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final TaskScheduler taskScheduler;
    private final BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService;

    public SpentTimeResetAllCommand(Server server, NotificationSettings notificationSettings, UserRepository userRepository, NotificationSender notificationSender, TaskScheduler taskScheduler, BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService) {
        this.server = server;
        this.notificationSettings = notificationSettings;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.taskScheduler = taskScheduler;
        this.bukkitPlayerSpentTimeService = bukkitPlayerSpentTimeService;
    }

    @Execute
    void resetSpentTimeForAllPlayers(@Context CommandSender sender) {
        if (sender instanceof Player player) {
            this.showGui(player);
            return;
        }

        this.resetGlobalSpentTime(sender);
    }

    private void showGui(Player player) {
        new ConfirmGui(this.taskScheduler)
                .create(ComponentUtil.createItalic("<red>Reset spent time of all users?"))
                .afterConfirm(event -> {
                    player.closeInventory();

                    this.resetGlobalSpentTime(player);
                })
                .closeAfterCancel()
                .open(player);
    }

    private void resetGlobalSpentTime(CommandSender sender) {
        this.userRepository.resetGlobalSpentTime()
                .thenAcceptAsync(v -> {
                    for (OfflinePlayer offlinePlayer : this.server.getOfflinePlayers()) {
                        this.bukkitPlayerSpentTimeService.resetSpentTime(offlinePlayer);
                    }

                    this.notificationSender.send(sender, this.notificationSettings.globalSpentTimeHasBeenReset);
                })
                .exceptionally(throwable -> {
                    this.notificationSender.send(sender, this.notificationSettings.globalSpentTimeResetError);
                    throw new RuntimeException(throwable);
                });
    }
}
