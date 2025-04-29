package com.github.imdmk.spenttime.gui;

import com.github.imdmk.spenttime.gui.configuration.item.GuiItemConfiguration;
import dev.triumphteam.gui.builder.gui.BaseGuiBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.function.Consumer;

public class AbstractGui {

    protected final GuiItemConfiguration settings;

    public AbstractGui(GuiItemConfiguration settings) {
        this.settings = settings;
    }

    protected BaseGuiBuilder<?, ?> createGuiBuilder(GuiType type) {
        return switch (type) {
            case STANDARD -> Gui.gui();
            case PAGINATED -> Gui.paginated();
            case SCROLLING_VERTICAL -> Gui.scrolling(ScrollType.VERTICAL);
            case SCROLLING_HORIZONTAL -> Gui.scrolling(ScrollType.HORIZONTAL);
            case DISABLED -> throw new IllegalArgumentException("Disabled gui cannot be created");
        };
    }

    protected Map<Integer, GuiItem> createNextPageItem(BaseGui gui, int slot) {
        return Map.of(slot, this.createNextPageItem(gui));
    }

    protected GuiItem createNextPageItem(BaseGui gui) {
        if (!(gui instanceof PaginatedGui paginatedGui)) {
            throw new IllegalArgumentException("Gui is not a paginated gui to create a next page item");
        }

        return this.settings.nextPageItem.asGuiItem(event -> {
            if (!paginatedGui.next()) {
                paginatedGui.updateItem(event.getSlot(), this.settings.noNextPageItem.asGuiItem());
            }
        });
    }

    protected Map<Integer, GuiItem> createPreviousPageItem(BaseGui gui, int slot) {
        return Map.of(slot, this.createPreviousPageItem(gui));
    }

    protected GuiItem createPreviousPageItem(BaseGui gui) {
        if (!(gui instanceof PaginatedGui paginatedGui)) {
            throw new IllegalArgumentException("Gui is not a paginated gui to create previous page item");
        }

        return this.settings.previousPageItem.asGuiItem(event -> {
            if (!paginatedGui.previous()) {
                paginatedGui.updateItem(event.getSlot(), this.settings.noPreviousPageItem.asGuiItem());
            }
        });
    }

    protected Map<Integer, GuiItem> createExitItem(int slot, Consumer<InventoryClickEvent> afterExit) {
        return Map.of(slot, this.createExitItem(afterExit));
    }

    protected GuiItem createExitItem(Consumer<InventoryClickEvent> afterExit) {
        return this.settings.exitItem.asGuiItem(afterExit::accept);
    }
}
