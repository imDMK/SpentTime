package com.github.imdmk.spenttime.command.implementation;

import com.github.imdmk.spenttime.gui.implementation.ConfirmGui;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.text.Formatter;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "spenttime reset")
@Permission("command.spenttime.reset")
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

    @Execute
    void reset(@Context CommandSender sender, @Arg User target) {
        if (sender instanceof Player player) {
            this.showGui(player, target);
            return;
        }

        this.taskScheduler.runAsync(() -> this.resetSpentTime(target));

        Formatter formatter = new Formatter()
                .placeholder("{PLAYER}", target.getName());

        this.notificationSender.send(sender, this.notificationSettings.targetSpentTimeHasBeenReset, formatter);
    }

    private void resetSpentTime(User target) {
        target.setSpentTime(0L);
        this.userRepository.save(target);

        this.server.getOfflinePlayer(target.getUuid()).setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
    }

    private void showGui(Player player, User target) {
        new ConfirmGui(this.taskScheduler)
                .create(ComponentUtil.createItalic("<red>Reset " + target.getName() + " player spent time?"))
                .afterConfirm(event -> {
                    player.closeInventory();

                    this.taskScheduler.runAsync(() -> this.resetSpentTime(target));

                    Formatter formatter = new Formatter()
                            .placeholder("{PLAYER}", target.getName());

                    this.notificationSender.send(player, this.notificationSettings.targetSpentTimeHasBeenReset, formatter);
                })
                .closeAfterCancel()
                .open(player);
    }
}
