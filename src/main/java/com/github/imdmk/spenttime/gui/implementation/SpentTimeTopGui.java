package com.github.imdmk.spenttime.gui.implementation;

import com.github.imdmk.spenttime.command.configuration.CommandConfiguration;
import com.github.imdmk.spenttime.configuration.implementation.MessageConfiguration;
import com.github.imdmk.spenttime.gui.GuiConfiguration;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.util.ComponentUtil;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.triumphteam.gui.builder.gui.BaseGuiBuilder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SpentTimeTopGui {

    private final Server server;
    private final CommandConfiguration commandConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final GuiConfiguration guiConfiguration;
    private final NotificationSender notificationSender;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;

    public SpentTimeTopGui(Server server, CommandConfiguration commandConfiguration, MessageConfiguration messageConfiguration, GuiConfiguration guiConfiguration, NotificationSender notificationSender, UserRepository userRepository, TaskScheduler taskScheduler) {
        this.server = server;
        this.commandConfiguration = commandConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.guiConfiguration = guiConfiguration;
        this.notificationSender = notificationSender;
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
    }

    public void open(Player player, List<User> topUsers) {
        BaseGui gui = this.createGuiBuilder()
                .title(this.guiConfiguration.title)
                .rows(6)
                .disableAllInteractions()
                .create();

        if (this.guiConfiguration.borderItemEnabled) {
            GuiItem sideGuiItem = ItemBuilder.from(this.guiConfiguration.borderItem).asGuiItem();

            gui.getFiller().fillBorder(sideGuiItem);
        }

        if (gui instanceof PaginatedGui paginatedGui) {
            GuiItem nextPageItem = this.createNextPageItem(paginatedGui);
            GuiItem previousPageItem = this.createPreviousPageItem(paginatedGui);

            paginatedGui.setItem(this.guiConfiguration.nextPageItemSlot, nextPageItem);
            paginatedGui.setItem(this.guiConfiguration.previousPageItemSlot, previousPageItem);
        }

        GuiItem exitGuiItem = ItemBuilder.from(this.guiConfiguration.exitItem)
                .asGuiItem(event -> gui.close(player));

        gui.setItem(this.guiConfiguration.exitItemSlot, exitGuiItem);

        boolean hasPermissionToReset = player.hasPermission(this.commandConfiguration.spentTimeResetPermission);

        AtomicInteger position = new AtomicInteger(0);

        for (User user : topUsers) {
            position.incrementAndGet();

            OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(user.getUuid());

            Component headItemTitle = this.replaceUserInformation(this.guiConfiguration.headItemTitle, user, position.get());

            List<Component> headItemLore = (hasPermissionToReset ? this.guiConfiguration.headItemLoreAdmin : this.guiConfiguration.headItemLore)
                    .stream()
                    .map(component -> this.replaceUserInformation(component, user, position.get()))
                    .toList();

            GuiItem guiItem = ItemBuilder.skull()
                    .owner(offlinePlayer)
                    .name(headItemTitle)
                    .lore(headItemLore)
                    .asGuiItem(event -> {
                        if (event.getClick() != this.guiConfiguration.headItemLoreAdminResetClick) {
                            return;
                        }

                        if (!hasPermissionToReset) {
                            return;
                        }

                        new ConfirmGui(this.taskScheduler)
                                .create(ComponentUtil.createItalic("<red>Reset " + user.getName() + " player spent time?"))
                                .afterConfirm(e -> {
                                    this.taskScheduler.runSync(() -> {
                                        user.setSpentTime(0L);
                                        this.userRepository.save(user);

                                        offlinePlayer.setStatistic(Statistic.PLAY_ONE_MINUTE, 0);
                                    });

                                    Notification notification = Notification.builder()
                                            .fromNotification(this.messageConfiguration.targetResetSpentTimeNotification)
                                            .placeholder("{PLAYER}", user.getName())
                                            .build();

                                    this.notificationSender.send(player, notification);

                                    gui.close(player);
                                })
                                .afterCancel(e -> this.open(player, topUsers))
                                .open(player);
                    });

            gui.addItem(guiItem);
        }

        this.taskScheduler.runSync(() -> gui.open(player));
    }

    private BaseGuiBuilder<?, ?> createGuiBuilder() {
        return switch (this.guiConfiguration.type) {
            case PAGINATED -> Gui.paginated();
            case STANDARD -> Gui.gui();
        };
    }

    private GuiItem createNextPageItem(PaginatedGui paginatedGui) {
        return ItemBuilder.from(this.guiConfiguration.nextPageItem)
                .asGuiItem(event -> {
                    if (!paginatedGui.next()) {
                        paginatedGui.updateItem(event.getSlot(), this.guiConfiguration.noNextPageItem);
                    }
                });
    }

    private GuiItem createPreviousPageItem(PaginatedGui paginatedGui) {
        return ItemBuilder.from(this.guiConfiguration.previousPageItem)
                .asGuiItem(event -> {
                    if (!paginatedGui.previous()) {
                        paginatedGui.updateItem(event.getSlot(), this.guiConfiguration.noPreviousPageItem);
                    }
                });
    }

    private Component replaceUserInformation(Component componentToReplace, User user, int userPosition) {
        return componentToReplace
                .replaceText(builder -> builder.matchLiteral("{PLAYER}")
                        .replacement(user.getName()))
                .replaceText(builder -> builder.matchLiteral("{POSITION}")
                        .replacement(String.valueOf(userPosition)))
                .replaceText(builder -> builder.matchLiteral("{TIME}")
                        .replacement(DurationUtil.toHumanReadable(user.getSpentTimeDuration())));
    }
}

