package com.github.imdmk.spenttime.feature.gui;

import com.github.imdmk.spenttime.gui.IdentifiableGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GuiProvider {

    private static final Map<String, IdentifiableGui> GUI_MAP = new HashMap<>();

    public static void registerGui(@NotNull IdentifiableGui gui) {
        GUI_MAP.put(gui.getIdentifier(), gui);
    }

    public static void openGui(@NotNull String identifier, @NotNull Player player) {
        IdentifiableGui gui = GUI_MAP.get(identifier);
        if (gui instanceof SimpleGui simpleGui) {
            simpleGui.open(player);
        }
        else {
            throw new IllegalArgumentException("Gui with identifier " + identifier + " is not SimpleGui");
        }
    }

    public static <T> void openGui(@NotNull String identifier, @NotNull Player viewer, @NotNull T parameter) {
        IdentifiableGui gui = GUI_MAP.get(identifier);
        if (gui instanceof ParameterizedGui<?> paramGui) {
            ((ParameterizedGui<T>) paramGui).open(viewer, parameter);
        }
        else {
            throw new IllegalArgumentException("Gui with identifier " + identifier + " is not ParameterizedGui");
        }
    }

}
