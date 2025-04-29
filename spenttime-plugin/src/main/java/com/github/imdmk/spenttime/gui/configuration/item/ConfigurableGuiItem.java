package com.github.imdmk.spenttime.gui.configuration.item;

import com.github.imdmk.spenttime.shared.Formatter;
import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurableGuiItem {

    private final Material material;
    private final Component name;
    private final List<Component> lore;
    private final int slot;

    private final Map<Enchantment, Integer> enchantments;

    private Formatter formatter;

    public ConfigurableGuiItem(Material material, Component name, List<Component> lore, int slot, Map<Enchantment, Integer> enchantments) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
        this.enchantments = enchantments;
    }

    public @NotNull Material material() {
        return this.material;
    }

    public @NotNull Component name() {
        return this.name;
    }

    public @NotNull List<Component> lore() {
        return this.lore;
    }

    public int slot() {
        return this.slot;
    }

    public @NotNull Map<Enchantment, Integer> enchantments() {
        return Collections.unmodifiableMap(this.enchantments);
    }

    @Contract("_ -> this")
    public ConfigurableGuiItem formatter(Formatter formatter) {
        this.formatter = formatter;
        return this;
    }

    public GuiItem asGuiItem() {
        return ItemBuilder.from(this.material)
                .name(this.formatter == null ? this.name : this.formatter.format(this.name))
                .lore(this.formatter == null ? this.lore : this.formatter.format(this.lore))
                .enchant(this.enchantments)
                .asGuiItem();
    }

    public GuiItem asGuiItem(GuiAction<InventoryClickEvent> event) {
        return ItemBuilder.from(this.material)
                .name(this.formatter == null ? this.name : this.formatter.format(this.name))
                .lore(this.formatter == null ? this.lore : this.formatter.format(this.lore))
                .enchant(this.enchantments)
                .asGuiItem(event);
    }

    public ItemStack asItemStack() {
        ItemBuilder builder = ItemBuilder.from(this.material);

        builder.name(this.formatter == null ? this.name : this.formatter.format(this.name));
        builder.lore(this.formatter == null ? this.lore : this.formatter.format(this.lore));
        builder.enchant(this.enchantments);

        return builder.build();
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static @NotNull Builder builderFrom(ConfigurableGuiItem item) {
        return new Builder()
                .material(item.material())
                .nameComponent(item.name())
                .loreComponent(item.lore())
                .slot(item.slot())
                .enchantment(item.enchantments());
    }

    public static class Builder {

        private Material material;
        private Component name;
        private List<Component> lore;
        private int slot;

        private Map<Enchantment, Integer> enchantments = new HashMap<>();

        private Builder() {
        }

        @Contract("_ -> this")
        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        @Contract("_ -> this")
        public Builder nameComponent(Component name) {
            this.name = name;
            return this;
        }

        @Contract("_ -> this")
        public Builder name(String name) {
            this.name = ComponentUtil.notItalic(name);
            return this;
        }

        @Contract("_ -> this")
        public Builder loreComponent(List<Component> lore) {
            this.lore = lore;
            return this;
        }

        @Contract("_ -> this")
        public Builder lore(List<String> lore) {
            this.lore = ComponentUtil.notItalic(lore);
            return this;
        }

        @Contract("_ -> this")
        public Builder lore(String... lore) {
            this.lore = ComponentUtil.notItalic(lore);
            return this;
        }

        @Contract("_ -> this")
        public Builder slot(int slot) {
            this.slot = slot;
            return this;
        }

        @Contract("_ -> this")
        public Builder enchantment(Map<Enchantment, Integer> enchantments) {
            this.enchantments = enchantments;
            return this;
        }

        @Contract("_,_ -> this")
        public Builder enchantment(Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public ConfigurableGuiItem build() {
            return new ConfigurableGuiItem(this.material, this.name, this.lore, this.slot, this.enchantments);
        }
    }
}

