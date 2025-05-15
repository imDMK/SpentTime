package com.github.imdmk.spenttime.feature.gui;

import com.github.imdmk.spenttime.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ParameterizedGui<T> extends IdentifiableGui {

    @NotNull BaseGui createGui(@NotNull Player viewer, @NotNull T parameter);

    default void prepareBorderItems(@NotNull BaseGui gui) {}

    default void prepareNavigationItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull T parameter) {}

    void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull T parameter);

    default void open(@NotNull Player viewer, @NotNull T parameter) {
        BaseGui gui = this.createGui(viewer, parameter);

        this.prepareBorderItems(gui);
        this.prepareItems(gui, viewer, parameter);
        this.prepareNavigationItems(gui, viewer, parameter);

        gui.open(viewer);
    }
}
