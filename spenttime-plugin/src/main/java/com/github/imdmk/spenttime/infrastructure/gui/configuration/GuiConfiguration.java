package com.github.imdmk.spenttime.infrastructure.gui.configuration;

// (importy bez zmian)

import com.github.imdmk.spenttime.configuration.ConfigSection;
import com.github.imdmk.spenttime.configuration.serializer.ComponentSerializer;
import com.github.imdmk.spenttime.configuration.serializer.SoundSerializer;
import com.github.imdmk.spenttime.gui.GuiType;
import com.github.imdmk.spenttime.infrastructure.gui.sound.GuiSound;
import com.github.imdmk.spenttime.infrastructure.gui.sound.GuiSoundSerializer;
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

    @Comment("# Enable border around all GUIs")
    public boolean fillBorder = true;

    @Comment("# Item used as the border around GUIs")
    public ConfigGuiItem borderItem = ConfigGuiItem.builder()
            .material(Material.GRAY_STAINED_GLASS_PANE)
            .name(" ")
            .lore(" ")
            .build();

    @Comment("# Item used to navigate to the next page")
    public ConfigGuiItem nextItem = ConfigGuiItem.builder()
            .material(Material.ARROW)
            .name("<green>Next page")
            .lore(" ", "<gray>Click <red>RIGHT <gray>to go to the next page", " ")
            .build();

    @Comment("# Item shown when there is no next page available")
    public ConfigGuiItem noNextItem = ConfigGuiItem.builder()
            .material(Material.BARRIER)
            .name("<red>There's no next page!")
            .lore(" ", "<red>Sorry, there is no next page available.", " ")
            .build();

    @Comment("# Item used to navigate to the previous page")
    public ConfigGuiItem previousItem = ConfigGuiItem.builder()
            .material(Material.ARROW)
            .name("<green>Previous page")
            .lore(" ", "<gray>Click <red>LEFT <gray>to go to the previous page", " ")
            .build();

    @Comment("# Item shown when there is no previous page available")
    public ConfigGuiItem noPreviousItem = ConfigGuiItem.builder()
            .material(Material.BARRIER)
            .name("<red>There's no previous page!")
            .lore(" ", "<red>Sorry, there is no previous page available.", " ")
            .build();

    @Comment("# Item used to exit the GUI")
    public ConfigGuiItem exitItem = ConfigGuiItem.builder()
            .material(Material.ACACIA_BUTTON)
            .name("<red>Exit GUI")
            .lore(" ", "<gray>Click <red>LEFT <gray>to exit this GUI", " ")
            .build();

    @Comment("# Configuration for the spent time top list GUI")
    public SpentTimeTopGuiConfiguration spentTimeTopGui = new SpentTimeTopGuiConfiguration();

    public static class SpentTimeTopGuiConfiguration extends OkaeriConfig {

        @Comment("# Title of the spent time top list GUI")
        public Component title = ComponentUtil.text("<red>Spent Time users top list");

        @Comment("# Type of GUI (e.g., PAGINATED, SINGLE_PAGE)")
        public GuiType type = GuiType.PAGINATED;

        @Comment({
                "# The head item name template",
                "# {PLAYER} - Player's name",
                "# {POSITION} - Player's position in the list",
                "# {TIME} - Player's spent time"
        })
        public ConfigGuiItem headItem = ConfigGuiItem.builder()
                .material(Material.PLAYER_HEAD)
                .name("<red>{POSITION}. <gray>Player <red>{PLAYER}")
                .lore(
                        "",
                        "<green>The player has spent <red>{TIME} <green>on the server<dark_gray>.",
                        ""
                )
                .build();

        @Comment({
                "# Lore shown for the head item when viewed by an admin",
                "# Supports placeholders: {PLAYER}, {POSITION}, {TIME}, {CLICK_REFRESH}, {CLICK_RESET}"
        })
        public List<Component> headItemAdminLore = ComponentUtil.notItalic(
                "",
                "<green>The player has spent <red>{TIME} <green>on the server<dark_gray>.",
                "",
                "<gray>Click <green>{CLICK_REFRESH} <gray>to force <green>refresh <gray>spent time.",
                "<gray>Click <red>{CLICK_RESET} <gray>to <red>reset {PLAYER} <gray>spent time."
        );

        @Comment({
                "# ClickType to reset a player's spent time via the GUI",
                "# Possible values: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html"
        })
        public ClickType headItemClickReset = ClickType.SHIFT_RIGHT;

        @Comment({
                "# ClickType to refresh a player's spent time via the GUI",
                "# Possible values: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html"
        })
        public ClickType headItemClickRefresh = ClickType.DOUBLE_CLICK;

        @Comment("# Permission required to reset a player's spent time via GUI")
        public String headItemPermissionReset = "spenttime.reset.gui";
    }

    @Comment("# Configuration for confirmation GUI dialogs")
    public ConfirmationGuiConfiguration confirmationGui = new ConfirmationGuiConfiguration();

    public static class ConfirmationGuiConfiguration extends OkaeriConfig {

        @Comment("# Title of the confirmation GUI")
        public Component title = ComponentUtil.text("<green>Are you sure?</green>");

        @Comment("# Number of rows in the confirmation GUI")
        public int rows = 6;

        @Comment("# Confirm button configuration")
        public ConfigGuiItem confirmItem = ConfigGuiItem.builder()
                .material(Material.GREEN_CONCRETE)
                .name("<green>Confirm")
                .lore(
                        " ",
                        "<green>Click to confirm action",
                        " "
                )
                .slot(21)
                .build();

        @Comment("# Cancel button configuration")
        public ConfigGuiItem cancelItem = ConfigGuiItem.builder()
                .material(Material.RED_CONCRETE)
                .name("<red>Cancel")
                .lore(
                        " ",
                        "<red>Click to cancel action",
                        " "
                )
                .slot(23)
                .build();
    }

    @Comment("# Sound played when player click gui item, to disable set volume to 0")
    public GuiSound clickSound = new GuiSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.1F, 1F);

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new ComponentSerializer());
            registry.register(new ConfigGuiSerializer());
            registry.register(new GuiSoundSerializer());
            registry.register(new SoundSerializer());
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "guiConfiguration.yml";
    }
}
