package com.github.imdmk.spenttime.infrastructure.gui.configuration;

import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ConfigGuiItem(@NotNull Material material,
                            @NotNull Component name, @NotNull List<Component> lore,
                            int slot,
                            @NotNull Map<Enchantment, Integer> enchantments,
                            @NotNull List<ItemFlag> flags) {

    public @NotNull GuiItem asGuiItem() {
        return this.asGuiItem(event -> {});
    }

    public @NotNull GuiItem asGuiItem(@NotNull GuiAction<InventoryClickEvent> event) {
        return ItemBuilder.from(this.material)
                .name(this.name)
                .lore(this.lore)
                .enchant(this.enchantments)
                .flags(this.flags.toArray(new ItemFlag[0]))
                .asGuiItem(event);
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Material material;
        private Component name;
        private List<Component> lore;
        private int slot;

        private final Map<Enchantment, Integer> enchantments = new HashMap<>();
        private final List<ItemFlag> flags = new ArrayList<>();

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
        public Builder enchantments(@NotNull Map<Enchantment, Integer> enchantments) {
            this.enchantments.putAll(enchantments);
            return this;
        }

        @Contract("_,_ -> this")
        public Builder enchantment(@NotNull Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        @Contract("_ -> this")
        public Builder flags(@NotNull ItemFlag... flags) {
            this.flags.addAll(List.of(flags));
            return this;
        }

        @Contract("_ -> this")
        public Builder flags(@NotNull List<ItemFlag> flags) {
            this.flags.addAll(flags);
            return this;
        }

        @Contract("_ -> this")
        public Builder from(@NotNull ConfigGuiItem item) {
            return this.material(item.material)
                    .nameComponent(item.name)
                    .loreComponent(item.lore)
                    .slot(item.slot)
                    .enchantments(item.enchantments)
                    .flags(item.flags.toArray(new ItemFlag[0]));
        }

        public @NotNull ConfigGuiItem build() {
            return new ConfigGuiItem(this.material, this.name, this.lore, this.slot, this.enchantments, this.flags);
        }
    }
}

