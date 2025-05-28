package com.github.imdmk.spenttime.infrastructure.gui;

import com.github.imdmk.spenttime.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a simple GUI that does not require a parameter to be created or populated.
 * Defines a standard lifecycle for opening such GUIs.
 */
public interface SimpleGui extends IdentifiableGui {

    /**
     * Creates a new instance of the GUI.
     *
     * @return the initialized {@link BaseGui} instance
     */
    @NotNull BaseGui createGui();

    /**
     * Optionally populates the border of the GUI.
     * This method has an empty default implementation and can be overridden.
     *
     * @param gui the GUI to modify
     */
    default void prepareBorderItems(@NotNull BaseGui gui) {
        // No-op by default
    }

    /**
     * Optionally adds navigation elements (e.g. exit button).
     * This method has an empty default implementation and can be overridden.
     *
     * @param gui    the GUI to modify
     * @param viewer the player viewing the GUI
     */
    default void prepareNavigationItems(@NotNull BaseGui gui, @NotNull Player viewer) {
        // No-op by default
    }

    /**
     * Optionally sets a default click handler for the GUI.
     * This method has an empty default implementation and can be overridden.
     *
     * @param gui    the GUI to modify
     * @param viewer the player viewing the GUI
     */
    default void defaultClickAction(@NotNull BaseGui gui, @NotNull Player viewer) {
        // No-op by default
    }

    /**
     * Prepares and populates the GUI with its core content.
     *
     * @param gui    the GUI to populate
     * @param viewer the player viewing the GUI
     */
    void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer);

    /**
     * Creates, prepares, and opens the GUI for the given viewer.
     * Execution order:
     * <ol>
     *     <li>{@link #createGui()}</li>
     *     <li>{@link #prepareBorderItems(BaseGui)}</li>
     *     <li>{@link #prepareItems(BaseGui, Player)}</li>
     *     <li>{@link #prepareNavigationItems(BaseGui, Player)}</li>
     *     <li>{@link #defaultClickAction(BaseGui, Player)}</li>
     *     <li>{@code gui.open(viewer)}</li>
     * </ol>
     *
     * @param viewer the player to show the GUI to
     */
    default void open(@NotNull Player viewer) {
        BaseGui gui = this.createGui();

        this.prepareBorderItems(gui);
        this.prepareItems(gui, viewer);
        this.prepareNavigationItems(gui, viewer);
        this.defaultClickAction(gui, viewer);

        gui.open(viewer);
    }
}
