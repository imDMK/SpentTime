package com.github.imdmk.spenttime.feature.gui.configuration;

import com.github.imdmk.spenttime.configuration.ConfigSection;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGui;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGuiConfiguration;
import com.github.imdmk.spenttime.feature.gui.configuration.item.ItemGuiSerializer;
import com.github.imdmk.spenttime.gui.GuiType;
import com.github.imdmk.spenttime.util.ComponentUtil;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiConfiguration extends ConfigSection {

    public SpentTimeTopGuiConfiguration spentTimeTopGui = new SpentTimeTopGuiConfiguration();

    public static class SpentTimeTopGuiConfiguration extends OkaeriConfig {

        public Component title = ComponentUtil.text("<red>");

        public GuiType type = GuiType.STANDARD;

        @Comment({
                "# The head item name",
                "# {PLAYER} - The player name",
                "# {POSITION} - The player position",
                "# {TIME} - The player time"
        })
        public ItemGui headItem = ItemGui.builder()
                .material(Material.PLAYER_HEAD)
                .name("<red>{POSITION}. <gray>Player <red>{PLAYER}")
                .lore(
                        "",
                        "<green>The player has spent <red>{TIME} <green>on the server<dark_gray>.",
                        ""
                )
                .build();

        @Comment({
                "# The head item name",
                "# {PLAYER} - The player name",
                "# {POSITION} - The player position",
                "# {TIME} - The player time"
        })
        public List<Component> headItemAdminLore = ComponentUtil.notItalic(
                "",
                "<green>The player has spent <red>{TIME} <green>on the server<dark_gray>.",
                "",
                "<red>Click {CLICK} <gray>to <red>reset {PLAYER} <gray>spent time."
        );

        @Comment({
                "# What type of button does the admin need to click to reset the player's spent time using the gui?",
                "# When the admin clicks a different button than the set one, nothing will happen",
                "# ClickTypes: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html"
        })
        public ClickType headItemClick = ClickType.SHIFT_RIGHT;

        public String headItemPermissionReset = "spenttime.reset.gui";
    }

    public ConfirmationGuiConfiguration confirmationGui = new ConfirmationGuiConfiguration();

    public static class ConfirmationGuiConfiguration extends OkaeriConfig {

        public Component title = ComponentUtil.text("<green>Are you sure?</green>");

        public int rows = 6;

        public ItemGui confirmItem = ItemGui.builder()
                .material(Material.GREEN_CONCRETE)
                .name("<green>Confirm")
                .slot(21)
                .build();

        public ItemGui cancelItem = ItemGui.builder()
                .material(Material.RED_CONCRETE)
                .name("<red>Cancel")
                .slot(23)
                .build();
    }

    public ItemGuiConfiguration items = new ItemGuiConfiguration();

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new ItemGuiSerializer());
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "guiConfiguration.yml";
    }
}
