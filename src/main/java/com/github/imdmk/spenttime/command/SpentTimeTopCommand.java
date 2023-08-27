package com.github.imdmk.spenttime.command;

import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import com.github.imdmk.spenttime.gui.GuiConfiguration;
import com.github.imdmk.spenttime.gui.implementation.SpentTimeTopGui;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
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

    private final GuiConfiguration guiConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final SpentTimeTopGui spentTimeTopGui;

    public SpentTimeTopCommand(GuiConfiguration guiConfiguration, MessageConfiguration messageConfiguration, UserRepository userRepository, NotificationSender notificationSender, SpentTimeTopGui spentTimeTopGui) {
        this.guiConfiguration = guiConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.spentTimeTopGui = spentTimeTopGui;
    }

    @Async
    @Execute(route = "top", required = 0)
    void showTopSpentTime(Player player) {
        List<User> topUsers = this.userRepository.findByOrderSpentTime(this.guiConfiguration.querySize);

        if (topUsers.isEmpty()) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.topSpentTimeIsEmpty);
            return;
        }

        if (this.guiConfiguration.enabled) {
            this.spentTimeTopGui.open(player, topUsers);
            return;
        }

        this.notificationSender.sendMessage(player, this.messageConfiguration.topSpentTimeListFirstNotification);

        AtomicInteger position = new AtomicInteger(0);

        for (User user : topUsers) {
            position.incrementAndGet();

            Notification notification = Notification.builder()
                    .fromNotification(this.messageConfiguration.topSpentTimeListNotification)
                    .placeholder("{POSITION}", position.get())
                    .placeholder("{PLAYER}", user.getName())
                    .placeholder("{TIME}", DurationUtil.toHumanReadable(user.getSpentTimeDuration()))
                    .build();

            this.notificationSender.sendMessage(player, notification);
        }
    }
}
