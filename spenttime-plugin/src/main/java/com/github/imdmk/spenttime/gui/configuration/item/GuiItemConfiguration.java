package com.github.imdmk.spenttime.gui.configuration.item;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;

public class GuiItemConfiguration extends OkaeriConfig {

    @Comment("# Include the item that is around the gui?")
    public boolean fillBorder = true;

    @Comment("# The item that is around the gui")
    public ConfigurableGuiItem borderItem = ConfigurableGuiItem.builder()
            .material(Material.GRAY_STAINED_GLASS_PANE)
            .name(" ")
            .lore(" ")
            .build();

    @Comment({"# Exit item", "# Set to -1 to disable"})
    public ConfigurableGuiItem exitItem = ConfigurableGuiItem.builder()
            .material(Material.ARROW)
            .name("<red>Quit")
            .slot(49)
            .build();

    @Comment({"# Previous page item", "# Set to -1 to disable"})
    public ConfigurableGuiItem previousPageItem = ConfigurableGuiItem.builder()
            .material(Material.ARROW)
            .name("<green>Previous page")
            .slot(46)
            .build();

    @Comment("# Used when the player clicks on the previous page but there is no page")
    public ConfigurableGuiItem noPreviousPageItem = ConfigurableGuiItem.builder()
            .material(Material.BARRIER)
            .name("<red>There is no previous page")
            .build();

    @Comment({"# Next page item", "# Set to -1 to disable"})
    public ConfigurableGuiItem nextPageItem = ConfigurableGuiItem.builder()
            .material(Material.ARROW)
            .name("<green>Next page")
            .slot(52)
            .build();

    @Comment("# Used when the player clicks on the next page but there is no page")
    public ConfigurableGuiItem noNextPageItem = ConfigurableGuiItem.builder()
            .material(Material.BARRIER)
            .name("<red>There is no next page")
            .build();
}
