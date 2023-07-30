package com.github.imdmk.spenttime.gui;

import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.util.DurationUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;

public class TopSpentTimeGui {

    private final Server server;
    private final PluginConfiguration pluginConfiguration;
    private final TaskScheduler taskScheduler;

    public TopSpentTimeGui(Server server, PluginConfiguration pluginConfiguration, TaskScheduler taskScheduler) {
        this.server = server;
        this.pluginConfiguration = pluginConfiguration;
        this.taskScheduler = taskScheduler;
    }

    public void open(Player player, List<User> topUsers, boolean async) {
        Gui spentTimeGui = Gui.gui()
                .title(this.pluginConfiguration.spentTimeGuiTitle)
                .rows(5)
                .disableAllInteractions()
                .create();

        if (this.pluginConfiguration.spentTimeGuiSideItemEnabled) {
            GuiItem sideGuiItem = ItemBuilder.from(this.pluginConfiguration.spentTimeGuiSideItem).asGuiItem();
            spentTimeGui.getFiller().fillBorder(sideGuiItem);
        }

        for (User user : topUsers) {
            Duration userTimeSpent = user.getDurationSpentTime();

            OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(user.getUuid());

            Component headItemTitle = this.pluginConfiguration.spentTimeGuiHeadItemTitle
                    .replaceText(builder -> builder
                            .matchLiteral("{PLAYER}")
                            .replacement(player.getName())
                    );

            List<Component> headItemLore = this.pluginConfiguration.spentTimeGuiHeadItemLore
                    .stream()
                    .map(component -> component.replaceText(builder -> builder
                            .matchLiteral("{TIME}")
                            .replacement(DurationUtil.toHumanReadable(userTimeSpent))
                    ))
                    .toList();


            GuiItem guiItem = ItemBuilder.skull()
                    .owner(offlinePlayer)
                    .name(headItemTitle)
                    .lore(headItemLore)
                    .asGuiItem();

            spentTimeGui.addItem(guiItem);
        }

        if (async) { //Opening gui cannot be asynchronous
            this.taskScheduler.runLater(() -> spentTimeGui.open(player));
        }
        else {
            spentTimeGui.open(player);
        }
    }
}
