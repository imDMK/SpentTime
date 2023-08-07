package com.github.imdmk.spenttime.command;

import com.github.imdmk.spenttime.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import com.github.imdmk.spenttime.gui.top.TopSpentTimeGui;
import com.github.imdmk.spenttime.gui.top.TopSpentTimePaginatedGui;
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

@Route(name = "spent-time")
public class SpentTimeTopCommand {

    private final GuiConfiguration guiConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final TopSpentTimeGui topSpentTimeGui;
    private final TopSpentTimePaginatedGui topSpentTimePaginatedGui;

    public SpentTimeTopCommand(GuiConfiguration guiConfiguration, MessageConfiguration messageConfiguration, UserRepository userRepository, NotificationSender notificationSender, TopSpentTimeGui topSpentTimeGui, TopSpentTimePaginatedGui topSpentTimePaginatedGui) {
        this.guiConfiguration = guiConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.topSpentTimeGui = topSpentTimeGui;
        this.topSpentTimePaginatedGui = topSpentTimePaginatedGui;
    }

    @Async
    @Execute(route = "top", required = 0)
    void showTopSpentTime(Player player) {
        List<User> topUsers = this.userRepository.findUsersByOrderSpentTime(this.guiConfiguration.querySize);

        if (topUsers.isEmpty()) {
            this.notificationSender.sendMessage(player, this.messageConfiguration.topSpentTimeIsEmpty);
            return;
        }

        if (this.guiConfiguration.enabled) {
            switch (this.guiConfiguration.guiType) {
                case STANDARD -> this.topSpentTimeGui.open(player, topUsers, true);
                case PAGINATED -> this.topSpentTimePaginatedGui.open(player, topUsers, true);
                default -> throw new IllegalStateException("Unexpected gui type value: " + this.guiConfiguration.guiType);
            }
            return;
        }

        this.notificationSender.sendMessage(player, this.messageConfiguration.topSpentTimeListFirstNotification);

        AtomicInteger position = new AtomicInteger(1);

        for (User user : topUsers) {
            Notification notification = Notification.builder()
                    .fromNotification(this.messageConfiguration.topSpentTimeListNotification)
                    .placeholder("{POSITION}", position.getAndIncrement())
                    .placeholder("{PLAYER}", user.getName())
                    .placeholder("{TIME}", DurationUtil.toHumanReadable(user.getSpentTimeDuration()))
                    .build();

            this.notificationSender.sendMessage(player, notification);
        }
    }
}
