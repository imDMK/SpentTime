package com.github.imdmk.spenttime.configuration.serializer;

import com.github.imdmk.spenttime.util.ComponentUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ItemMetaSerializer implements ObjectSerializer<ItemMeta> {

    @Override
    public boolean supports(@NotNull Class<? super ItemMeta> type) {
        return ItemMeta.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull ItemMeta itemMeta, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        if (itemMeta.hasDisplayName()) {
            Component displayName = ComponentUtil.text(itemMeta.getDisplayName());
            data.add("display-name", displayName, Component.class);
        }

        if (itemMeta.getLore() != null && itemMeta.hasLore()) {
            List<Component> lore = ComponentUtil.text(itemMeta.getLore());
            data.addCollection("lore", lore, Component.class);
        }

        if (itemMeta.hasEnchants()) {
            data.addAsMap("enchantments", itemMeta.getEnchants(), Enchantment.class, Integer.class);
        }

        if (!itemMeta.getItemFlags().isEmpty()) {
            data.addCollection("item-flags", itemMeta.getItemFlags(), ItemFlag.class);
        }
    }

    @Override
    public ItemMeta deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        ItemBuilder itemBuilder = ItemBuilder.from(Material.STONE);

        Optional.ofNullable(data.get("display-name", Component.class)).ifPresent(itemBuilder::name);
        Optional.ofNullable(data.getAsList("lore", Component.class)).ifPresent(itemBuilder::lore);

        Optional.ofNullable(data.getAsMap("enchantments", Enchantment.class, Integer.class)).ifPresent(itemBuilder::enchant);
        Optional.ofNullable(data.getAsList("item-flags", ItemFlag.class))
                .map(itemFlags -> itemFlags.toArray(new ItemFlag[0]))
                .ifPresent(itemBuilder::flags);

        return itemBuilder.build().getItemMeta();
    }
}
