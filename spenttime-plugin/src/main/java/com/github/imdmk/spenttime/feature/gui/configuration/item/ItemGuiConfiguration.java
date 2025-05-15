package com.github.imdmk.spenttime.feature.gui.configuration.item;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;

public class ItemGuiConfiguration extends OkaeriConfig {

    @Comment("# Include the item that is around the gui?")
    public boolean fillBorder = true;

    @Comment("# The item that is around the gui")
    public ItemGui borderItem = ItemGui.builder()
            .material(Material.GRAY_STAINED_GLASS_PANE)
            .name("<dark_gray>empty! what you looking for?")
            .lore("")
            .build();

    @Comment({"# Exit item", "# Set to -1 to disable"})
    public ItemGui exitItem = ItemGui.builder()
            .material(Material.ARROW)
            .name("<red>Quit")
            .lore(" ")
            .slot(49)
            .build();

    public PaginatedGuiItemConfiguration paginatedGui = new PaginatedGuiItemConfiguration();

    public static class PaginatedGuiItemConfiguration extends OkaeriConfig {

        @Comment({"# Previous page item", "# Set to -1 to disable"})
        public ItemGui previousPageItem = ItemGui.builder()
                .material(Material.ARROW)
                .name("<green>Previous page")
                .slot(46)
                .build();

        @Comment("# Used when the player clicks on the previous page but there is no page")
        public ItemGui noPreviousPageItem = ItemGui.builder()
                .material(Material.BARRIER)
                .name("<red>There is no previous page")
                .build();

        @Comment({"# Next page item", "# Set to -1 to disable"})
        public ItemGui nextPageItem = ItemGui.builder()
                .material(Material.ARROW)
                .name("<green>Next page")
                .slot(52)
                .build();

        @Comment("# Used when the player clicks on the next page but there is no page")
        public ItemGui noNextPageItem = ItemGui.builder()
                .material(Material.BARRIER)
                .name("<red>There is no next page")
                .build();

    }
}
