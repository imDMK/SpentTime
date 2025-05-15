package com.github.imdmk.spenttime.feature.gui.configuration;

import com.github.imdmk.spenttime.configuration.ConfigSection;
import com.github.imdmk.spenttime.configuration.serializer.ComponentSerializer;
import com.github.imdmk.spenttime.configuration.serializer.ItemMetaSerializer;
import com.github.imdmk.spenttime.configuration.serializer.ItemStackSerializer;
import com.github.imdmk.spenttime.configuration.serializer.SoundSerializer;
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
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiConfiguration extends ConfigSection {

    public SpentTimeTopGuiConfiguration spentTimeTopGui = new SpentTimeTopGuiConfiguration();

    public static class SpentTimeTopGuiConfiguration extends OkaeriConfig {

        public Component title = ComponentUtil.text("<red>Spent Time users top list");

        public GuiType type = GuiType.PAGINATED;

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
                "<gray>Click <green>{CLICK_REFRESH} <gray>to force <green>refresh <gray>spent time.",
                "<gray>Click <red>{CLICK_RESET} <gray>to <red>reset {PLAYER} <gray>spent time."
        );

        @Comment({
                "# What type of button does the admin need to click to reset the player's spent time using the gui?",
                "# ClickTypes: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html"
        })
        public ClickType headItemClickReset = ClickType.SHIFT_RIGHT;

        @Comment({
                "# What type of button does the admin need to click to force refresh the player's spent time using the gui?",
                "# ClickTypes: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html"
        })
        public ClickType headItemClickRefresh = ClickType.DOUBLE_CLICK;

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

    public GuiSoundConfiguration sound = new GuiSoundConfiguration();

    public static class GuiSoundConfiguration extends OkaeriConfig {

        public boolean enabled = true;

        public Sound sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

        public float volume = 1.1F;

        public float pitch = 1F;
    }

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new ComponentSerializer());
            registry.register(new ItemMetaSerializer());
            registry.register(new ItemStackSerializer());
            registry.register(new ItemGuiSerializer());
            registry.register(new SoundSerializer());
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "guiConfiguration.yml";
    }
}
