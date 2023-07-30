package com.github.imdmk.spenttime.configuration;

import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PluginConfiguration extends OkaeriConfig {

    public boolean checkForUpdate = true;

    public String spentTimeResetTimeCommandPermission = "spenttime.resettime";

    @Comment("# Spent time GUI")
    public boolean spentTimeGuiEnabled = true;

    public Component spentTimeGuiTitle = ComponentUtil.createItalic("<red>Top spent time<dark_gray>:");

    @Comment({
            "# The title of head item in spent time gui",
            "# {PLAYER} - The player name"
    })
    public Component spentTimeGuiHeadItemTitle = ComponentUtil.createItalic("<gray>Player <red>{PLAYER}");

    @Comment({
            "# The lore of head item in spent time gui",
            "# {TIME} - The spent time"
    })
    public List<Component> spentTimeGuiHeadItemLore = List.of(
        ComponentUtil.createItalic(""),
        ComponentUtil.createItalic("<green>The player has spent <red>{TIME} <green>on the server<dark_gray>."),
        ComponentUtil.createItalic("")
    );

    @Comment("# Include the item that is around the gui?")
    public boolean spentTimeGuiSideItemEnabled = true;
    @Comment("# The item that is around the gui")
    public ItemStack spentTimeGuiSideItem = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).build();

    public MessageConfiguration messageConfiguration = new MessageConfiguration();
    public DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
}
