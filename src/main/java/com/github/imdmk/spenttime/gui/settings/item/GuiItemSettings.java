package com.github.imdmk.spenttime.gui.settings.item;

import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiItemSettings extends OkaeriConfig {

    @Comment({
            "# Exit item",
            "# Set to -1 to disable",
    })
    public int exitItemSlot = 49;
    public ItemStack exitItem = ItemBuilder.from(Material.ACACIA_BUTTON)
            .name(ComponentUtil.createItalic("<red>Quit"))
            .build();

    @Comment({
            "# The head item name",
            "# {PLAYER} - The player name",
            "# {POSITION} - The player position",
            "# {TIME} - The player time"
    })
    public String headName = "<!italic><red>{POSITION}. <gray>Player <red>{PLAYER}";

    @Comment("# The head item default lore")
    public List<String> headLore = List.of(
            "",
            "<!italic><green>The player has spent <red>{TIME} <green>on the server<dark_gray>.",
            ""
    );

    @Comment("# The head item lore displayed when a player has permission to reset a player's spent time")
    public List<String> headLoreAdmin = List.of(
            "",
            "<!italic><green>The player has spent <red>{TIME} <green>on the server<dark_gray>.",
            "",
            "<!italic><red>Click {CLICK} <gray>to <red>reset {PLAYER} <gray>spent time."
    );

    @Comment({
            "# What type of button does the admin need to click to reset the player's spent time using the gui?",
            "# When the admin clicks a different button than the set one, nothing will happen",
            "# ClickTypes: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html"
    })
    public ClickType headClickType = ClickType.SHIFT_RIGHT;

    @Comment("# Include the item that is around the gui?")
    public boolean fillBorder = true;

    @Comment("# The item that is around the gui")
    public ItemStack borderItem = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).build();
}
