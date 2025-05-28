package com.github.imdmk.spenttime.infrastructure.gui;

import com.github.imdmk.spenttime.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a generic GUI that requires a parameter to be initialized and populated.
 * Acts as a template for all GUIs that are parameterized, defining a default open lifecycle.
 *
 * @param <T> the type of parameter used for populating the GUI
 */
public interface ParameterizedGui<T> extends IdentifiableGui {

    /**
     * Creates a new instance of the GUI.
     *
     * @param viewer    the player viewing the GUI
     * @param parameter the parameter used to customize the GUI
     * @return the initialized {@link BaseGui} instance
     */
    @NotNull BaseGui createGui(@NotNull Player viewer, @NotNull T parameter);

    /**
     * Optionally fills the border of the GUI with decorative items.
     * This method has an empty default implementation and can be overridden.
     *
     * @param gui the GUI to modify
     */
    default void prepareBorderItems(@NotNull BaseGui gui) {
        // No-op by default
    }

    /**
     * Optionally adds navigation elements (e.g. next/previous/exit buttons).
     * This method has an empty default implementation and can be overridden.
     *
     * @param gui       the GUI to modify
     * @param viewer    the player viewing the GUI
     * @param parameter the context parameter
     */
    default void prepareNavigationItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull T parameter) {
        // No-op by default
    }

    /**
     * Optionally sets a default action for clicks on empty slots or the background.
     * This method has an empty default implementation and can be overridden.
     *
     * @param gui    the GUI to modify
     * @param viewer the player viewing the GUI
     */
    default void defaultClickAction(@NotNull BaseGui gui, @NotNull Player viewer) {
        // No-op by default
    }

    /**
     * Prepares and populates the GUI with core content based on the parameter.
     *
     * @param gui       the GUI to populate
     * @param viewer    the player viewing the GUI
     * @param parameter the context parameter
     */
    void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull T parameter);

    /**
     * Initializes and opens the GUI for the specified player.
     * Follows a fixed sequence: create → borders → items → navigation → click actions → open.
     *
     * @param viewer    the player to show the GUI to
     * @param parameter the parameter used to customize the GUI
     */
    default void open(@NotNull Player viewer, @NotNull T parameter) {
        BaseGui gui = this.createGui(viewer, parameter);

        this.prepareBorderItems(gui);
        this.prepareItems(gui, viewer, parameter);
        this.prepareNavigationItems(gui, viewer, parameter);
        this.defaultClickAction(gui, viewer);

        gui.open(viewer);
    }
}
