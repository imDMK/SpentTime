package com.github.imdmk.spenttime.command.implementation;

import com.github.imdmk.spenttime.gui.implementation.SpentTimeTopGui;
import com.github.imdmk.spenttime.gui.settings.GuiSettings;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.rollczi.litecommands.command.async.Async;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Route(name = "spenttime")
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
    @Execute(route = "top", required = 0)
    void showTopSpentTime(Player player) {
        List<User> topUsers = this.userRepository.findByOrderSpentTime(this.guiSettings.querySize);

        if (topUsers.isEmpty()) {
            this.notificationSender.send(player, this.notificationSettings.topSpentTimeIsEmpty);
            return;
        }

        if (this.guiSettings.enabled) {
            this.spentTimeTopGui.open(player, topUsers);
            return;
        }

        this.notificationSender.send(player, this.notificationSettings.topSpentTimeListFirstNotification);

        AtomicInteger position = new AtomicInteger(0);

        for (User user : topUsers) {
            position.incrementAndGet();

            Notification notification = Notification.builder()
                    .fromNotification(this.notificationSettings.topSpentTimeListNotification)
                    .placeholder("{POSITION}", position.get())
                    .placeholder("{PLAYER}", user.getName())
                    .placeholder("{TIME}", DurationUtil.toHumanReadable(user.getSpentTimeDuration()))
                    .build();

            this.notificationSender.send(player, notification);
        }
    }
}
