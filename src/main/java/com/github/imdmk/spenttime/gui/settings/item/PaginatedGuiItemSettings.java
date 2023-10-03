package com.github.imdmk.spenttime.gui.settings.item;

import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PaginatedGuiItemSettings extends OkaeriConfig {

    @Comment({
            "# Previous page item",
            "# NOTE: Only used when spent time top query is greater than 10",
            "# Set to -1 to disable"
    })
    public int previousPageItemSlot = 46;
    public ItemStack previousPageItem = ItemBuilder.from(Material.ARROW)
            .name(ComponentUtil.createItalic("<green>Previous page"))
            .build();

    @Comment("# Used when the player clicks on the previous page but there is no page")
    public ItemStack noPreviousPageItem = ItemBuilder.from(Material.BARRIER)
            .name(ComponentUtil.createItalic("<red>There is no previous page"))
            .build();

    @Comment({
            "# Next page item",
            "# NOTE: Only used when spent time top query is greater than 10",
            "# Set to -1 to disable"
    })
    public int nextPageItemSlot = 52;
    public ItemStack nextPageItem = ItemBuilder.from(Material.ARROW)
            .name(ComponentUtil.createItalic("<green>Next page"))
            .build();

    @Comment("# Used when the player clicks on the next page but there is no page")
    public ItemStack noNextPageItem = ItemBuilder.from(Material.BARRIER)
            .name(ComponentUtil.createItalic("<red>There is no next page"))
            .build();
}
