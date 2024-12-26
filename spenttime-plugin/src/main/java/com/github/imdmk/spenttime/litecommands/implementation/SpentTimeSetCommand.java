package com.github.imdmk.spenttime.litecommands.implementation;

import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationFormatter;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@Command(name = "spenttime set")
@Permission("command.spenttime.set")
public class SpentTimeSetCommand {

    private final NotificationSettings notificationSettings;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService;

    public SpentTimeSetCommand(NotificationSettings notificationSettings, UserRepository userRepository, NotificationSender notificationSender, BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService) {
        this.notificationSettings = notificationSettings;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.bukkitPlayerSpentTimeService = bukkitPlayerSpentTimeService;
    }

    @Execute
    void setTime(@Context CommandSender sender, @Arg User target, @Arg Duration time) {
        target.setSpentTime(time);

        this.userRepository.save(target)
                .thenAcceptAsync(updatedUser -> {
                    this.bukkitPlayerSpentTimeService.setSpentTime(target.getUuid(), time);

                    Notification notification = new NotificationFormatter()
                            .notification(this.notificationSettings.targetSpentTimeHasBeenSet)
                            .placeholder("{PLAYER}", target.getName())
                            .placeholder("{TIME}", DurationUtil.toHumanReadable(time))
                            .build();

                    this.notificationSender.send(sender, notification);
                })
                .exceptionally(throwable -> {
                    this.notificationSender.send(sender, this.notificationSettings.targetSpentTimeSetError);
                    throw new RuntimeException(throwable);
                });
    }
}
