package com.github.imdmk.spenttime.gui.implementation;

import com.github.imdmk.spenttime.command.settings.CommandSettings;
import com.github.imdmk.spenttime.gui.settings.GuiItemSettings;
import com.github.imdmk.spenttime.gui.settings.GuiSettings;
import com.github.imdmk.spenttime.gui.settings.ScrollingGuiSettings;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.text.Formatter;
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

public class SpentTimeTopGui {

    private final Server server;
    private final CommandSettings commandSettings;
    private final NotificationSettings notificationSettings;
    private final GuiSettings guiSettings;
    private final ScrollingGuiSettings scrollingGuiSettings;
    private final GuiItemSettings guiItemSettings;
    private final NotificationSender notificationSender;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;

    public SpentTimeTopGui(Server server, CommandSettings commandSettings, NotificationSettings notificationSettings, GuiSettings guiSettings, ScrollingGuiSettings scrollingGuiSettings, GuiItemSettings guiItemSettings, NotificationSender notificationSender, UserRepository userRepository, TaskScheduler taskScheduler) {
        this.server = server;
        this.commandSettings = commandSettings;
        this.notificationSettings = notificationSettings;
        this.guiSettings = guiSettings;
        this.scrollingGuiSettings = scrollingGuiSettings;
        this.guiItemSettings = guiItemSettings;
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
            int nextPageItemSlot = this.guiItemSettings.nextPageItemSlot;
            GuiItem nextPageItem = this.createNextPageItem(paginatedGui);

            int previousPageItemSlot = this.guiItemSettings.previousPageItemSlot;
            GuiItem previousPageItem = this.createPreviousPageItem(paginatedGui);

            if (nextPageItemSlot > 0) {
                paginatedGui.setItem(nextPageItemSlot, nextPageItem);
            }
            if (previousPageItemSlot > 0) {
                paginatedGui.setItem(previousPageItemSlot, previousPageItem);
            }
        }

        GuiItem exitGuiItem = ItemBuilder.from(this.guiItemSettings.exitItem)
                .asGuiItem(event -> gui.close(player));

        gui.setItem(this.guiItemSettings.exitItemSlot, exitGuiItem);

        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);

            OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(user.getUuid());

            Formatter formatter = new Formatter()
                    .placeholder("{PLAYER}", user.getName())
                    .placeholder("{POSITION}", i + 1)
                    .placeholder("{TIME}", DurationUtil.toHumanReadable(user.getSpentTimeDuration()))
                    .placeholder("{CLICK}", this.guiItemSettings.headClickType.name());

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
                        if (event.getClick() != this.guiItemSettings.headClickType) {
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

                                    this.notificationSender.send(player, this.notificationSettings.targetResetSpentTimeNotification, formatter);

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
            case STANDARD -> Gui.gui();
            case PAGINATED -> Gui.paginated();
            case SCROLLING -> Gui.scrolling(this.scrollingGuiSettings.scrollType);
            case DISABLED -> throw new IllegalArgumentException("");
        };
    }

    private GuiItem createNextPageItem(PaginatedGui paginatedGui) {
        return ItemBuilder.from(this.guiItemSettings.nextPageItem)
                .asGuiItem(event -> {
                    if (!paginatedGui.next()) {
                        paginatedGui.updateItem(event.getSlot(), this.guiItemSettings.noNextPageItem);
                    }
                });
    }

    private GuiItem createPreviousPageItem(PaginatedGui paginatedGui) {
        return ItemBuilder.from(this.guiItemSettings.previousPageItem)
                .asGuiItem(event -> {
                    if (!paginatedGui.previous()) {
                        paginatedGui.updateItem(event.getSlot(), this.guiItemSettings.noPreviousPageItem);
                    }
                });
    }

    private boolean hasPermissionToReset(Player player) {
        return player.hasPermission(this.commandSettings.spentTimeResetPermission);
    }
}

