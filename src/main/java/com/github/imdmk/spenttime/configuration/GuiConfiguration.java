package com.github.imdmk.spenttime.configuration;

import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiConfiguration extends OkaeriConfig {

    public boolean enabled = true;

    @Comment({
            "# This determines how many players are to be displayed in the Player top",
            "# WARNING: Increasing this value may increase the consumption of database server resources",
            "# recommended value: 10"
    })
    public int querySize = 10;

    @Comment({
            "# The type of gui",
            "# Available types: ",
            "# STANDARD - Standard, basic gui",
            "# PAGINATED - Gui with pages; Useful when you want to display several pages of tops"
    })
    public GuiType guiType = GuiType.STANDARD;

    public Component title = ComponentUtil.createItalic("<red>Top spent time<dark_gray>:");

    @Comment({
            "# The title of head item in spent time gui",
            "# {POSITION} - The player position",
            "# {PLAYER} - The player name"
    })
    public Component headItemTitle = ComponentUtil.createItalic("<red>{POSITION}. <gray>Player <red>{PLAYER}");

    @Comment({
            "# The lore of head item in spent time gui",
            "# {TIME} - The spent time"
    })
    public List<Component> headItemLore = List.of(
            ComponentUtil.createItalic(""),
            ComponentUtil.createItalic("<green>The player has spent <red>{TIME} <green>on the server<dark_gray>."),
            ComponentUtil.createItalic("")
    );

    @Comment("# Include the item that is around the gui?")
    public boolean borderItemEnabled = true;
    @Comment("# The item that is around the gui")
    public ItemStack borderItem = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).build();

    @Comment({
            "# Exit item slot",
            "# Set to -1 to disable",
            "# USEFUL: https://i.imgur.com/yuNaucx.png"
    })
    public int exitItemSlot = 49;
    public ItemStack exitItem = ItemBuilder.from(Material.ACACIA_BUTTON)
            .name(ComponentUtil.createItalic("<red>Quit"))
            .build();

    @Comment({
            "# Spent time paginated gui configuration",
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

    @Comment("# Set to -1 to disable")
    public int nextPageItemSlot = 52;
    public ItemStack nextPageItem = ItemBuilder.from(Material.ARROW)
            .name(ComponentUtil.createItalic("<green>Next page"))
            .build();

    @Comment("# Used when the player clicks on the next page but there is no page")
    public ItemStack noNextPageItem = ItemBuilder.from(Material.BARRIER)
            .name(ComponentUtil.createItalic("<red>There is no next page"))
            .build();

    public enum GuiType {
        STANDARD, PAGINATED
    }
}
