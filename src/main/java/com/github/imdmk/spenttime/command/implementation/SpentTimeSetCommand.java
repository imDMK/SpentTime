package com.github.imdmk.spenttime.command.implementation;

import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.text.Formatter;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import com.github.imdmk.spenttime.util.PlayerUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@Command(name = "spenttime set")
public class SpentTimeSetCommand {

    private final Server server;
    private final NotificationSettings notificationSettings;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final TaskScheduler taskScheduler;

    public SpentTimeSetCommand(Server server, NotificationSettings notificationSettings, UserRepository userRepository, NotificationSender notificationSender, TaskScheduler taskScheduler) {
        this.server = server;
        this.notificationSettings = notificationSettings;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.taskScheduler = taskScheduler;
    }

    @Execute
    @Permission("command.spenttime.set")
    void setTime(@Context CommandSender sender, @Arg User target, @Arg Duration time) {
        this.taskScheduler.runAsync(() -> this.setSpentTime(target, time));

        Formatter formatter = new Formatter()
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{TIME}", DurationUtil.toHumanReadable(time));

        this.notificationSender.send(sender, this.notificationSettings.targetSpentTimeHasBeenSet, formatter);
    }

    private void setSpentTime(User target, Duration time) {
        target.setSpentTime(time);
        this.userRepository.save(target);

        OfflinePlayer targetPlayer = this.server.getOfflinePlayer(target.getUuid());
        PlayerUtil.setSpentTime(targetPlayer, time);
    }
}

