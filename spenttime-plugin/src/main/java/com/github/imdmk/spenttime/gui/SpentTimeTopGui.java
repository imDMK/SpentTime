package com.github.imdmk.spenttime.gui;

import com.github.imdmk.spenttime.gui.settings.GuiItemSettings;
import com.github.imdmk.spenttime.gui.settings.GuiSettings;
import com.github.imdmk.spenttime.gui.settings.ScrollingGuiSettings;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.text.Formatter;
import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
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
import org.bukkit.entity.Player;

import java.util.List;

public class SpentTimeTopGui {

    private final Server server;

    private final NotificationSettings notificationSettings;
    private final GuiSettings guiSettings;
    private final ScrollingGuiSettings scrollingGuiSettings;
    private final GuiItemSettings guiItemSettings;

    private final NotificationSender notificationSender;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;
    private final BukkitPlayerSpentTimeService playerSpentTimeService;

    public SpentTimeTopGui(Server server, NotificationSettings notificationSettings, GuiSettings guiSettings, ScrollingGuiSettings scrollingGuiSettings, GuiItemSettings guiItemSettings, NotificationSender notificationSender, UserRepository userRepository, TaskScheduler taskScheduler, BukkitPlayerSpentTimeService playerSpentTimeService) {
        this.server = server;
        this.notificationSettings = notificationSettings;
        this.guiSettings = guiSettings;
        this.scrollingGuiSettings = scrollingGuiSettings;
        this.guiItemSettings = guiItemSettings;
        this.notificationSender = notificationSender;
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
        this.playerSpentTimeService = playerSpentTimeService;
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
            User topUser = topUsers.get(i);

            String topUserName = topUser.getName();
            int topUserPosition = i + 1;
            String topUserSpentTime = DurationUtil.toHumanReadable(topUser.getSpentTimeDuration());
            String headClickTypeName = this.guiItemSettings.headClickType.name();

            OfflinePlayer topUserPlayer = this.server.getOfflinePlayer(topUser.getUuid());

            Formatter formatter = new Formatter()
                    .placeholder("{PLAYER}", topUserName)
                    .placeholder("{POSITION}", topUserPosition)
                    .placeholder("{TIME}", topUserSpentTime)
                    .placeholder("{CLICK}", headClickTypeName);

            Component headItemTitle = ComponentUtil.deserialize(formatter.format(this.guiItemSettings.headName));

            List<Component> headItemLore = (this.hasPermissionToReset(player) ? this.guiItemSettings.headLoreAdmin : this.guiItemSettings.headLore)
                    .stream()
                    .map(formatter::format)
                    .map(ComponentUtil::deserialize)
                    .toList();

            GuiItem guiItem = ItemBuilder.skull()
                    .owner(topUserPlayer)
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
                                .create(ComponentUtil.createItalic("<red>Reset " + topUserName + " player spent time?"))
                                .afterConfirm(e -> {
                                    gui.close(player);

                                    topUser.setSpentTime(0L);
                                    this.userRepository.save(topUser).thenAcceptAsync(updatedTopUser -> {
                                        this.playerSpentTimeService.resetSpentTime(topUserPlayer);

                                        this.notificationSender.send(player, this.notificationSettings.targetSpentTimeHasBeenReset, formatter);
                                    })
                                    .exceptionally(throwable -> {
                                        this.notificationSender.send(player, this.notificationSettings.targetSpentTimeResetError, formatter);
                                        throw new RuntimeException(throwable);
                                    });
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
            case DISABLED -> throw new IllegalArgumentException("Disabled gui cannot be opened");
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
        return player.hasPermission("command.spenttime.reset");
    }
}
