package com.github.imdmk.spenttime.feature.gui.configuration.item;

import com.github.imdmk.spenttime.shared.Formatter;
import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ItemGui {

    private final Material material;
    private final Component name;
    private final List<Component> lore;
    private final int slot;

    private final Map<Enchantment, Integer> enchantments;

    private Formatter formatter;

    public ItemGui(
            @NotNull Material material,
            @NotNull Component name,
            @Nullable List<Component> lore,
            int slot,
            @Nullable Map<Enchantment, Integer> enchantments
    ) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
        this.enchantments = enchantments;
    }

    public ItemGui(
            @NotNull Material material,
            @NotNull Component name,
            @Nullable List<Component> lore,
            int slot,
            @Nullable Map<Enchantment, Integer> enchantments,
            @Nullable Formatter formatter
    ) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
        this.enchantments = enchantments;
        this.formatter = formatter;
    }

    public @NotNull Material material() {
        return this.material;
    }

    public @NotNull Component name() {
        return this.name;
    }

    public @NotNull List<Component> lore() {
        if (this.lore == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(this.lore);
    }

    public int slot() {
        return this.slot;
    }

    public @NotNull Map<Enchantment, Integer> enchantments() {
        if (this.enchantments == null) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(this.enchantments);
    }

    public @Nullable Formatter formatter() {
        return this.formatter;
    }

    public @NotNull GuiItem asGuiItem() {
        return ItemBuilder.from(this.material)
                .name(this.format(this.name))
                .lore(this.format(this.lore()))
                .enchant(this.enchantments())
                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem();
    }

    public @NotNull GuiItem asGuiItem(GuiAction<InventoryClickEvent> event) {
        return ItemBuilder.from(this.material)
                .name(this.format(this.name()))
                .lore(this.format(this.lore()))
                .enchant(this.enchantments())
                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .asGuiItem(event);
    }

    public @NotNull ItemStack asItemStack() {
        return ItemBuilder.from(this.material)
                .name(this.format(this.name()))
                .lore(this.format(this.lore()))
                .enchant(this.enchantments())
                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .build();
    }

    private Component format(@NotNull Component component) {
        if (this.formatter == null) {
            return component;
        }

        return this.formatter.format(component);
    }

    private List<Component> format(@NotNull List<Component> components) {
        if (this.formatter == null) {
            return components;
        }

        return this.formatter.format(components);
    }

    public @NotNull Builder toBuilder() {
        return new Builder()
                .material(this.material())
                .nameComponent(this.name())
                .loreComponent(this.lore())
                .slot(this.slot())
                .enchantment(this.enchantments());
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Material material;
        private Component name;
        private List<Component> lore;
        private int slot;

        private Map<Enchantment, Integer> enchantments = Map.of();

        private Formatter formatter;

        private Builder() {}

        @Contract("_ -> this")
        public Builder material(@NotNull Material material) {
            this.material = material;
            return this;
        }

        @Contract("_ -> this")
        public Builder nameComponent(@NotNull Component name) {
            this.name = name;
            return this;
        }

        @Contract("_ -> this")
        public Builder name(@NotNull String name) {
            this.name = ComponentUtil.notItalic(name);
            return this;
        }

        @Contract("_ -> this")
        public Builder loreComponent(@NotNull List<Component> lore) {
            this.lore = lore;
            return this;
        }

        @Contract("_ -> this")
        public Builder lore(@NotNull List<String> lore) {
            this.lore = ComponentUtil.notItalic(lore);
            return this;
        }

        @Contract("_ -> this")
        public Builder lore(@NotNull String... lore) {
            this.lore = ComponentUtil.notItalic(lore);
            return this;
        }

        @Contract("_ -> this")
        public Builder slot(int slot) {
            this.slot = slot;
            return this;
        }

        @Contract("_ -> this")
        public Builder enchantment(@NotNull Map<Enchantment, Integer> enchantments) {
            this.enchantments = enchantments;
            return this;
        }

        @Contract("_,_ -> this")
        public Builder enchantment(@NotNull Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        @Contract("_ -> this")
        public Builder formatter(@NotNull Formatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public ItemGui build() {
            return new ItemGui(this.material, this.name, this.lore, this.slot, this.enchantments, this.formatter);
        }
    }
}

