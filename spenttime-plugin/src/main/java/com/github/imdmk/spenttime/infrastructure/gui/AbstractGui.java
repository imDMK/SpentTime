package com.github.imdmk.spenttime.infrastructure.gui;

import com.github.imdmk.spenttime.gui.GuiType;
import com.github.imdmk.spenttime.infrastructure.gui.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.task.TaskScheduler;
import dev.triumphteam.gui.builder.gui.BaseGuiBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Abstract base class providing reusable logic for GUI construction and navigation.
 * Designed to be extended by specific GUI implementations.
 */
public abstract class AbstractGui {

    protected final GuiConfiguration config;
    protected final TaskScheduler taskScheduler;

    /**
     * Constructs a new instance of the AbstractGui with required dependencies.
     *
     * @param config         the GUI configuration used to generate items
     * @param taskScheduler  the task scheduler for delayed operations
     */
    protected AbstractGui(@NotNull GuiConfiguration config, @NotNull TaskScheduler taskScheduler) {
        this.config = Objects.requireNonNull(config, "guiConfiguration cannot be null");
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "taskScheduler cannot be null");
    }

    /**
     * Sets the "next page" navigation item in a paginated GUI.
     *
     * @param gui the GUI to configure
     */
    protected void setNextPageItem(@NotNull BaseGui gui) {
        gui.setItem(this.getNextPageItemSlot(gui.getRows()), this.createNextPageItem(gui));
    }

    /**
     * Returns the slot index where the "next page" item should be placed based on row count.
     */
    protected int getNextPageItemSlot(int rows) {
        return switch (rows) {
            case 3 -> 25;
            case 4 -> 34;
            case 5 -> 43;
            case 6 -> 52;
            default -> throw new IllegalStateException("Unexpected row size: " + rows);
        };
    }

    /**
     * Creates the "next page" navigation item with click behavior.
     *
     * @param gui the GUI where the item will be placed
     * @return a {@link GuiItem} representing the next page button
     */
    protected GuiItem createNextPageItem(@NotNull BaseGui gui) {
        if (!(gui instanceof PaginatedGui paginatedGui)) {
            throw new IllegalArgumentException("Gui must be a paginated GUI to create a next page item.");
        }

        return this.config.nextItem.asGuiItem(event -> {
            if (!paginatedGui.next()) {
                paginatedGui.updateItem(event.getSlot(), this.config.noNextItem.asGuiItem());
                this.restoreItemLater(event, gui, this.createNextPageItem(gui));
            }
        });
    }

    /**
     * Sets the "previous page" navigation item in a paginated GUI.
     *
     * @param gui the GUI to configure
     */
    protected void setPreviousPageItem(@NotNull BaseGui gui) {
        gui.setItem(this.getPreviousPageItemSlot(gui.getRows()), this.createPreviousPageItem(gui));
    }

    /**
     * Returns the slot index for the "previous page" item based on GUI row count.
     */
    protected int getPreviousPageItemSlot(int rows) {
        return switch (rows) {
            case 3 -> 19;
            case 4 -> 28;
            case 5 -> 37;
            case 6 -> 46;
            default -> throw new IllegalStateException("Unexpected row size: " + rows);
        };
    }

    /**
     * Creates the "previous page" navigation item with click behavior.
     *
     * @param gui the GUI where the item will be placed
     * @return a {@link GuiItem} representing the previous page button
     */
    protected GuiItem createPreviousPageItem(@NotNull BaseGui gui) {
        if (!(gui instanceof PaginatedGui paginatedGui)) {
            throw new IllegalArgumentException("Gui must be a paginated GUI to create a previous page item.");
        }

        return this.config.previousItem.asGuiItem(event -> {
            if (!paginatedGui.previous()) {
                paginatedGui.updateItem(event.getSlot(), this.config.noPreviousItem.asGuiItem());
                this.restoreItemLater(event, gui, this.createPreviousPageItem(gui));
            }
        });
    }

    /**
     * Sets the "exit" item in the GUI that closes the inventory or performs other logic.
     *
     * @param gui  the GUI to configure
     * @param exit the action to perform on click
     */
    protected void setExitPageItem(@NotNull BaseGui gui, @NotNull Consumer<InventoryClickEvent> exit) {
        gui.setItem(this.getExitPageItemSlot(gui.getRows()), this.createExitPageItem(exit));
    }

    /**
     * Returns the slot index for the "exit" item based on GUI row count.
     */
    protected int getExitPageItemSlot(int rows) {
        return switch (rows) {
            case 3 -> 22;
            case 4 -> 31;
            case 5 -> 40;
            case 6 -> 49;
            default -> throw new IllegalStateException("Unexpected row size: " + rows);
        };
    }

    /**
     * Creates the "exit" item with the specified click handler.
     *
     * @param exit the action to perform when the item is clicked
     * @return a {@link GuiItem} representing the exit button
     */
    protected GuiItem createExitPageItem(@NotNull Consumer<InventoryClickEvent> exit) {
        return this.config.exitItem.asGuiItem(exit::accept);
    }

    /**
     * Schedules an item to be restored after a delay.
     *
     * @param event the original click event
     * @param gui   the GUI where the item will be restored
     * @param item  the item to restore
     */
    protected void restoreItemLater(@NotNull InventoryClickEvent event, @NotNull BaseGui gui, @NotNull GuiItem item) {
        this.taskScheduler.runLaterAsync(() -> gui.updateItem(event.getSlot(), item), 60L);
    }

    /**
     * Creates a new GUI builder for the specified {@link GuiType}.
     *
     * @param type the GUI type to construct
     * @return a GUI builder for that type
     */
    protected BaseGuiBuilder<?, ?> createGuiBuilder(@NotNull GuiType type) {
        return switch (type) {
            case STANDARD -> Gui.gui();
            case PAGINATED -> Gui.paginated();
            case SCROLLING_VERTICAL -> Gui.scrolling(ScrollType.VERTICAL);
            case SCROLLING_HORIZONTAL -> Gui.scrolling(ScrollType.HORIZONTAL);
        };
    }

}
