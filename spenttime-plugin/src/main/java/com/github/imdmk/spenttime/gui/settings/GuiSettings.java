package com.github.imdmk.spenttime.gui.settings;

import com.github.imdmk.spenttime.gui.GuiType;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class GuiSettings extends OkaeriConfig {

    @Comment({
            "# This determines how many players are to be displayed in the player top",
            "# WARNING: Increasing this value may increase the consumption of database server resources",
            "# Recommended value: 10"
    })
    public int querySize = 10;

    @Comment({
            "# The type of gui",
            "# Available types:",
            "# DISABLED - The list of players will be sent in the chat",
            "# STANDARD - Standard Gui that should be used when the player list does not exceed 10",
            "# PAGINATED - A Gui with pages that allow you to move between pages through items",
            "# SCROLLING - A Gui that allows you to scroll through items"
    })
    public GuiType type = GuiType.STANDARD;

    @Comment("# The title of spent time gui")
    public String title = "<red>Top spent time<dark_gray>:";

    @Comment({"#", "# Gui item settings", "#"})
    public GuiItemSettings guiItemSettings = new GuiItemSettings();
}
