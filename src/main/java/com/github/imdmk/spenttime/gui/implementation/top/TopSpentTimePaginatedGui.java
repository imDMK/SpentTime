package com.github.imdmk.spenttime.gui.implementation.top;

import com.github.imdmk.spenttime.gui.GuiConfiguration;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TopSpentTimePaginatedGui {

    private final Server server;
    private final GuiConfiguration guiConfiguration;
    private final TaskScheduler taskScheduler;

    public TopSpentTimePaginatedGui(Server server, GuiConfiguration guiConfiguration, TaskScheduler taskScheduler) {
        this.server = server;
        this.guiConfiguration = guiConfiguration;
        this.taskScheduler = taskScheduler;
    }

    public void open(Player player, List<User> topUsers) {
        PaginatedGui paginatedGui = Gui.paginated()
                .title(this.guiConfiguration.title)
                .rows(6)
                .disableAllInteractions()
                .create();

        if (this.guiConfiguration.borderItemEnabled) {
            GuiItem sideGuiItem = ItemBuilder.from(this.guiConfiguration.borderItem).asGuiItem();

            paginatedGui.getFiller().fillBorder(sideGuiItem);
        }

        GuiItem exitGuiItem = ItemBuilder.from(this.guiConfiguration.exitItem)
                .asGuiItem(event -> paginatedGui.close(player));

        GuiItem nextPageItem = this.createNextPageItem(paginatedGui);
        GuiItem previousPageItem = this.createPreviousPageItem(paginatedGui);

        paginatedGui.setItem(this.guiConfiguration.exitItemSlot, exitGuiItem);
        paginatedGui.setItem(this.guiConfiguration.nextPageItemSlot, nextPageItem);
        paginatedGui.setItem(this.guiConfiguration.previousPageItemSlot, previousPageItem);

        AtomicInteger position = new AtomicInteger(0);

        for (User user : topUsers) {
            position.incrementAndGet();

            String userTimeSpent = DurationUtil.toHumanReadable(user.getSpentTimeDuration());
            OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(user.getUuid());

            Component headItemTitle = this.guiConfiguration.headItemTitle
                    .replaceText(builder -> builder.matchLiteral("{POSITION}").replacement(String.valueOf(position.get())))
                    .replaceText(builder -> builder.matchLiteral("{PLAYER}").replacement(player.getName()))
                    .replaceText(builder -> builder.matchLiteral("{TIME}").replacement(userTimeSpent));

            List<Component> headItemLore = this.guiConfiguration.headItemLore
                    .stream()
                    .map(component -> component
                            .replaceText(builder -> builder.matchLiteral("{POSITION}").replacement(String.valueOf(position.get())))
                            .replaceText(builder -> builder.matchLiteral("{PLAYER}").replacement(player.getName()))
                            .replaceText(builder -> builder.matchLiteral("{TIME}").replacement(userTimeSpent))
                    )
                    .toList();

            GuiItem guiItem = ItemBuilder.skull()
                    .owner(offlinePlayer)
                    .name(headItemTitle)
                    .lore(headItemLore)
                    .asGuiItem();

            paginatedGui.addItem(guiItem);
        }

        this.taskScheduler.runSync(() -> paginatedGui.open(player));
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
}
