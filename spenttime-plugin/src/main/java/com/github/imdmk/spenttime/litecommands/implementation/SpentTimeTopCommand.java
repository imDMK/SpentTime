package com.github.imdmk.spenttime.litecommands.implementation;

import com.github.imdmk.spenttime.gui.GuiType;
import com.github.imdmk.spenttime.gui.SpentTimeTopGui;
import com.github.imdmk.spenttime.gui.settings.GuiSettings;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationFormatter;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

import java.util.List;

@Command(name = "spenttime top")
@Permission("command.spenttime.top")
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

    @Execute
    void showTopSpentTime(@Context Player player) {
        this.userRepository.findByOrderSpentTime(this.guiSettings.querySize)
                .thenAccept(topUsers -> {
                    if (topUsers.isEmpty()) {
                        this.notificationSender.send(player, this.notificationSettings.topSpentTimeIsEmpty);
                        return;
                    }

                    if (this.guiSettings.type == GuiType.DISABLED) {
                        this.sendSpentTimeTop(player, topUsers);
                        return;
                    }

                    this.spentTimeTopGui.open(player, topUsers);
                })
                .exceptionally(throwable -> {
                    this.notificationSender.send(player, this.notificationSettings.topSpentTimeQueryError);
                    throw new RuntimeException(throwable);
                });
    }

    private void sendSpentTimeTop(Player player, List<User> topUsers) {
        this.notificationSender.send(player, this.notificationSettings.topSpentTimeListFirst);

        for (int i = 0; i < topUsers.size(); i++) {
            int position = i + 1;
            User user = topUsers.get(i);
            String userName = user.getName();
            String userSpentTime = DurationUtil.toHumanReadable(user.getSpentTimeDuration());

            Notification notification = new NotificationFormatter()
                    .notification(this.notificationSettings.topSpentTimeList)
                    .placeholder("{POSITION}", position)
                    .placeholder("{PLAYER}", userName)
                    .placeholder("{TIME}", userSpentTime)
                    .build();

            this.notificationSender.send(player, notification);
        }
    }
}
