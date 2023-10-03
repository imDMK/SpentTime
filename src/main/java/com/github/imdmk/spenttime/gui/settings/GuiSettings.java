package com.github.imdmk.spenttime.gui.settings;

import com.github.imdmk.spenttime.gui.GuiType;
import com.github.imdmk.spenttime.gui.settings.item.GuiItemSettings;
import com.github.imdmk.spenttime.gui.settings.item.PaginatedGuiItemSettings;
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
            "# Should the gui be enabled?",
            "# If you disable the gui, a list of top players in the chat will be displayed"
    })
    public boolean enabled = true;

    @Comment({
            "# The type of gui",
            "# Available types: ",
            "# STANDARD - Standard, basic gui",
            "# PAGINATED - Gui with pages; Useful when you want to display several pages of tops"
    })
    public GuiType type = GuiType.STANDARD;

    @Comment("# The title of spent time gui")
    public String title = "<red>Top spent time<dark_gray>:";

    @Comment({"#", "# Gui item settings", "#"})
    public GuiItemSettings guiItemSettings = new GuiItemSettings();

    @Comment({"#", "# Paginated gui item settings", "#"})
    public PaginatedGuiItemSettings paginatedGuiItemSettings = new PaginatedGuiItemSettings();
}
