package com.github.imdmk.spenttime.gui.implementation;

import com.github.imdmk.spenttime.command.settings.CommandSettings;
import com.github.imdmk.spenttime.gui.settings.GuiSettings;
import com.github.imdmk.spenttime.gui.settings.item.GuiItemSettings;
import com.github.imdmk.spenttime.gui.settings.item.PaginatedGuiItemSettings;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
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
import panda.utilities.text.Formatter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SpentTimeTopGui {

    private final Server server;
    private final CommandSettings commandSettings;
    private final NotificationSettings notificationSettings;
    private final GuiSettings guiSettings;
    private final GuiItemSettings guiItemSettings;
    private final PaginatedGuiItemSettings paginatedGuiItemSettings;
    private final NotificationSender notificationSender;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;

    public SpentTimeTopGui(Server server, CommandSettings commandSettings, NotificationSettings notificationSettings, GuiSettings guiSettings, GuiItemSettings guiItemSettings, PaginatedGuiItemSettings paginatedGuiItemSettings, NotificationSender notificationSender, UserRepository userRepository, TaskScheduler taskScheduler) {
        this.server = server;
        this.commandSettings = commandSettings;
        this.notificationSettings = notificationSettings;
        this.guiSettings = guiSettings;
        this.guiItemSettings = guiItemSettings;
        this.paginatedGuiItemSettings = paginatedGuiItemSettings;
        this.notificationSender = notificationSender;
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
    }

    public void open(Player player, List<User> topUsers) {
        BaseGui gui = this.createGuiBuilder()
                .title(ComponentUtil.deserialize(this.guiSettings.title))
                .rows(6)
                .disableAllInteractions()
                .create();

        if (this.guiItemSettings.fillBorder) {
            GuiItem sideGuiItem = ItemBuilder.from(this.guiItemSettings.borderItem).asGuiItem();

            gui.getFiller().fillBorder(sideGuiItem);
        }

        if (gui instanceof PaginatedGui paginatedGui) {
            GuiItem nextPageItem = this.createNextPageItem(paginatedGui);
            GuiItem previousPageItem = this.createPreviousPageItem(paginatedGui);

            paginatedGui.setItem(this.paginatedGuiItemSettings.nextPageItemSlot, nextPageItem);
            paginatedGui.setItem(this.paginatedGuiItemSettings.previousPageItemSlot, previousPageItem);
        }

        GuiItem exitGuiItem = ItemBuilder.from(this.guiSettings.guiItemSettings.exitItem)
                .asGuiItem(event -> gui.close(player));

        gui.setItem(this.guiItemSettings.exitItemSlot, exitGuiItem);

        AtomicInteger position = new AtomicInteger(0);

        for (User user : topUsers) {
            position.incrementAndGet();

            OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(user.getUuid());

            Formatter formatter = new Formatter()
                    .register("{PLAYER}", user.getName())
                    .register("{POSITION}", position)
                    .register("{TIME}", DurationUtil.toHumanReadable(user.getSpentTimeDuration()))
                    .register("{CLICK}", this.guiItemSettings.headClickType.name());

            Component headItemTitle = ComponentUtil.deserialize(formatter.format(this.guiItemSettings.headName));

            List<Component> headItemLore = (this.hasPermissionToReset(player) ? this.guiItemSettings.headLoreAdmin : this.guiItemSettings.headLore)
                    .stream()
                    .map(formatter::format)
                    .map(ComponentUtil::deserialize)
                    .toList();

            GuiItem guiItem = ItemBuilder.skull()
                    .owner(offlinePlayer)
                    .name(headItemTitle)
                    .lore(headItemLore)
                    .asGuiItem(event -> {
                        if (event.getClick() != this.guiSettings.guiItemSettings.headClickType) {
                            return;
                        }

                        if (!this.hasPermissionToReset(player)) {
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
                                            .fromNotification(this.notificationSettings.targetResetSpentTimeNotification)
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
        return switch (this.guiSettings.type) {
            case PAGINATED -> Gui.paginated();
            case STANDARD -> Gui.gui();
        };
    }

    private GuiItem createNextPageItem(PaginatedGui paginatedGui) {
        return ItemBuilder.from(this.guiSettings.paginatedGuiItemSettings.nextPageItem)
                .asGuiItem(event -> {
                    if (!paginatedGui.next()) {
                        paginatedGui.updateItem(event.getSlot(), this.guiSettings.paginatedGuiItemSettings.noNextPageItem);
                    }
                });
    }

    private GuiItem createPreviousPageItem(PaginatedGui paginatedGui) {
        return ItemBuilder.from(this.guiSettings.paginatedGuiItemSettings.previousPageItem)
                .asGuiItem(event -> {
                    if (!paginatedGui.previous()) {
                        paginatedGui.updateItem(event.getSlot(), this.guiSettings.paginatedGuiItemSettings.noPreviousPageItem);
                    }
                });
    }

    private boolean hasPermissionToReset(Player player) {
        return player.hasPermission(this.commandSettings.spentTimeResetPermission);
    }
}

