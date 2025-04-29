package com.github.imdmk.spenttime.user.gui;

import com.github.imdmk.spenttime.gui.GuiType;
import com.github.imdmk.spenttime.gui.configuration.item.ConfigurableGuiItem;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class SpentTimeTopGuiConfiguration extends OkaeriConfig {

    public String permissionToResetSpentTime = "command.spenttime.reset";

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

    @Comment({
            "# The head item",
            "# {PLAYER} - The player name",
            "# {POSITION} - The player position",
            "# {TIME} - The player time"
    })
    public ConfigurableGuiItem headItem = ConfigurableGuiItem.builder()
            .name("<red>{POSITION}. <gray>Player <red>{PLAYER}")
            .lore(
                    "",
                    "<!italic><green>The player has spent <red>{TIME} <green>on the server<dark_gray>.",
                    ""
            )
            .build();

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
    public ClickType headAdminClick = ClickType.SHIFT_RIGHT;
}
