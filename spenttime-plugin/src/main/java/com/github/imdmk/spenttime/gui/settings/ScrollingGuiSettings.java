package com.github.imdmk.spenttime.gui.settings;

import dev.triumphteam.gui.components.ScrollType;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class ScrollingGuiSettings extends OkaeriConfig {

    @Comment({
            "# Specifies the direction in which the player list should move",
            "# Available directions:",
            "# HORIZONTAL - The items will be moving horizontally",
            "# VERTICAL - The items list will be moving vertically"
    })
    public ScrollType scrollType = ScrollType.VERTICAL;
}
