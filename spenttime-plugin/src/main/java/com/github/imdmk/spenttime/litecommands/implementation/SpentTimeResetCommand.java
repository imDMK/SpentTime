package com.github.imdmk.spenttime.litecommands.implementation;

import com.github.imdmk.spenttime.gui.ConfirmGui;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationFormatter;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "spenttime reset")
@Permission("command.spenttime.reset")
public class SpentTimeResetCommand {

    private final NotificationSettings notificationSettings;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final TaskScheduler taskScheduler;
    private final BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService;

    public SpentTimeResetCommand(NotificationSettings notificationSettings, UserRepository userRepository, NotificationSender notificationSender, TaskScheduler taskScheduler, BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService) {
        this.notificationSettings = notificationSettings;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.taskScheduler = taskScheduler;
        this.bukkitPlayerSpentTimeService = bukkitPlayerSpentTimeService;
    }

    @Execute
    void resetSpentTimeTarget(@Context CommandSender sender, @Arg User target) {
        if (sender instanceof Player player) {
            this.showGui(player, target);
            return;
        }

        this.resetSpentTime(sender, target);
    }

    private void showGui(Player player, User target) {
        new ConfirmGui(this.taskScheduler)
                .create(ComponentUtil.createItalic("<red>Reset " + target.getName() + " player spent time?"))
                .afterConfirm(event -> {
                    player.closeInventory();

                    this.resetSpentTime(player, target);
                })
                .closeAfterCancel()
                .open(player);
    }

    private void resetSpentTime(CommandSender sender, User target) {
        target.setSpentTime(0L);

        this.userRepository.save(target)
                .thenAcceptAsync(updatedUser -> {
                    this.bukkitPlayerSpentTimeService.resetSpentTime(target.getUuid());

                    Notification notification = new NotificationFormatter()
                            .notification(this.notificationSettings.targetSpentTimeHasBeenReset)
                            .placeholder("{PLAYER}", target.getName())
                            .build();

                    this.notificationSender.send(sender, notification);
                })
                .exceptionally(throwable -> {
                    this.notificationSender.send(sender, this.notificationSettings.targetSpentTimeResetError);
                    throw new RuntimeException(throwable);
                });
    }
}
