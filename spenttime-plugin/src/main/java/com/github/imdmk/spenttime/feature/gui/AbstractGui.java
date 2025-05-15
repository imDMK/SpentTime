package com.github.imdmk.spenttime.feature.gui;

import com.github.imdmk.spenttime.feature.gui.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGuiConfiguration;
import com.github.imdmk.spenttime.gui.GuiType;
import com.github.imdmk.spenttime.task.TaskScheduler;
import dev.triumphteam.gui.builder.gui.BaseGuiBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class AbstractGui {

    protected final ItemGuiConfiguration config;
    protected final TaskScheduler taskScheduler;

    public AbstractGui(
            @NotNull ItemGuiConfiguration config,
            @NotNull TaskScheduler taskScheduler
    ) {
        this.config = Objects.requireNonNull(config, "item gui configuration cannot be null");
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "task scheduler cannot be null");
    }

    protected void playSoundIfEnabled(BaseGui gui, Player player, GuiConfiguration.GuiSoundConfiguration soundConfiguration) {
        if (!soundConfiguration.enabled) {
            return;
        }

        gui.setDefaultClickAction(event -> {
            if (event.getCurrentItem() == null) {
                return;
            }

            player.playSound(player, soundConfiguration.sound, soundConfiguration.volume, soundConfiguration.pitch);
        });
    }

    protected BaseGuiBuilder<?, ?> createGuiBuilder(@NotNull GuiType type) {
        return switch (type) {
            case STANDARD -> Gui.gui();
            case PAGINATED -> Gui.paginated();
            case SCROLLING_VERTICAL -> Gui.scrolling(ScrollType.VERTICAL);
            case SCROLLING_HORIZONTAL -> Gui.scrolling(ScrollType.HORIZONTAL);
        };
    }

    protected Map<Integer, GuiItem> createExitItem(int slot, @NotNull Consumer<InventoryClickEvent> exit) {
        if (slot < 0) {
            return Collections.emptyMap();
        }

        return Map.of(slot, this.createExitItem(exit));
    }

    protected GuiItem createExitItem(@NotNull Consumer<InventoryClickEvent> exit) {
        return this.config.exitItem.asGuiItem(exit::accept);
    }

    protected Map<Integer, GuiItem> createNextPageItem(@NotNull BaseGui gui, int slot) {
        return Map.of(slot, this.createNextPageItem(gui));
    }

    protected GuiItem createNextPageItem(@NotNull BaseGui gui) {
        if (!(gui instanceof PaginatedGui paginatedGui)) {
            throw new IllegalArgumentException("Gui is not a paginated gui to create a next page item");
        }

        return this.config.paginatedGui.nextPageItem.asGuiItem(event -> {
            if (!paginatedGui.next()) {
                paginatedGui.updateItem(event.getSlot(), this.config.paginatedGui.noNextPageItem.asGuiItem());
                this.restoreItemLater(event, gui, this.createNextPageItem(gui));
            }
        });
    }

    protected Map<Integer, GuiItem> createPreviousPageItem(@NotNull BaseGui gui, int slot) {
        return Map.of(slot, this.createPreviousPageItem(gui));
    }

    protected GuiItem createPreviousPageItem(@NotNull BaseGui gui) {
        if (!(gui instanceof PaginatedGui paginatedGui)) {
            throw new IllegalArgumentException("Gui is not a paginated gui to create previous page item");
        }

        return this.config.paginatedGui.previousPageItem.asGuiItem(event -> {
            if (!paginatedGui.previous()) {
                paginatedGui.updateItem(event.getSlot(), this.config.paginatedGui.noPreviousPageItem.asGuiItem());
                this.restoreItemLater(event, gui, this.createPreviousPageItem(gui));
            }
        });
    }

    protected void restoreItemLater(@NotNull InventoryClickEvent event, @NotNull BaseGui gui, @NotNull GuiItem item) {
        this.taskScheduler.runLaterAsync(() -> gui.updateItem(event.getSlot(), item), 60L);
    }
}
