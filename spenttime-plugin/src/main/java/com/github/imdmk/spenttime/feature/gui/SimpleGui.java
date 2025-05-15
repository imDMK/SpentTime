package com.github.imdmk.spenttime.feature.gui;

import com.github.imdmk.spenttime.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface SimpleGui extends IdentifiableGui {

    @NotNull BaseGui createGui();

    default void prepareBorderItems(@NotNull BaseGui gui) {}

    default void prepareNavigationItems(@NotNull BaseGui gui, @NotNull Player viewer) {}

    default void defaultClickAction(@NotNull BaseGui gui, @NotNull Player viewer) {}

    void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer);

    default void open(@NotNull Player viewer) {
        BaseGui gui = this.createGui();

        this.prepareBorderItems(gui);
        this.prepareItems(gui, viewer);
        this.prepareNavigationItems(gui, viewer);
        this.defaultClickAction(gui, viewer);

        gui.open(viewer);
    }
}
