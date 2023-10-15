package com.github.imdmk.spenttime.command.implementation;

import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import com.github.imdmk.spenttime.text.Formatter;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@Route(name = "spenttime")
public class SpentTimeSetCommand {

    private final Server server;
    private final NotificationSettings notificationSettings;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;

    public SpentTimeSetCommand(Server server, NotificationSettings notificationSettings, UserRepository userRepository, NotificationSender notificationSender) {
        this.server = server;
        this.notificationSettings = notificationSettings;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
    }

    @Execute(route = "set")
    @Permission("command.spenttime.set")
    void setTime(CommandSender sender, @Arg User target, @Arg @Name("time") Duration time) {
        long timeMillis = time.toMillis() * 20;

        this.server.getOfflinePlayer(target.getUuid()).setStatistic(Statistic.PLAY_ONE_MINUTE, (int) timeMillis);

        target.setSpentTime(timeMillis);
        this.userRepository.save(target);

        Formatter formatter = new Formatter()
                .placeholder("{PLAYER}", target.getName())
                .placeholder("{TIME}", DurationUtil.toHumanReadable(time));

        this.notificationSender.send(sender, this.notificationSettings.targetSpentTimeHasBeenSet, formatter);
    }
}
