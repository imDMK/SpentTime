package com.github.imdmk.spenttime.command.implementation;

import com.github.imdmk.spenttime.gui.GuiType;
import com.github.imdmk.spenttime.gui.implementation.SpentTimeTopGui;
import com.github.imdmk.spenttime.gui.settings.GuiSettings;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import com.github.imdmk.spenttime.text.Formatter;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

import java.util.List;

@Command(name = "spenttime top")
public class SpentTimeTopCommand {

    private final GuiSettings guiSettings;
    private final NotificationSettings notificationSettings;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final SpentTimeTopGui spentTimeTopGui;

    public SpentTimeTopCommand(GuiSettings guiSettings, NotificationSettings notificationSettings, UserRepository userRepository, NotificationSender notificationSender, SpentTimeTopGui spentTimeTopGui) {
        this.guiSettings = guiSettings;
        this.notificationSettings = notificationSettings;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.spentTimeTopGui = spentTimeTopGui;
    }

    @Async
    @Execute
    @Permission("command.spenttime.top")
    void showTopSpentTime(@Context Player player) {
        List<User> topUsers = this.userRepository.findByOrderSpentTime(this.guiSettings.querySize);

        if (topUsers.isEmpty()) {
            this.notificationSender.send(player, this.notificationSettings.topSpentTimeIsEmpty);
            return;
        }

        if (this.guiSettings.type == GuiType.DISABLED) {
            this.showSpentTimeTop(player, topUsers);
            return;
        }

        this.spentTimeTopGui.open(player, topUsers);
    }

    private void showSpentTimeTop(Player player, List<User> topUsers) {
        this.notificationSender.send(player, this.notificationSettings.topSpentTimeListFirst);

        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);

            Formatter formatter = new Formatter()
                    .placeholder("{POSITION}", i + 1)
                    .placeholder("{PLAYER}", user.getName())
                    .placeholder("{TIME}", DurationUtil.toHumanReadable(user.getSpentTimeDuration()));

            this.notificationSender.send(player, this.notificationSettings.topSpentTimeList, formatter);
        }
    }
}
