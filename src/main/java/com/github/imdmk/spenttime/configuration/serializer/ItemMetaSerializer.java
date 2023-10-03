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
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemMetaSerializer implements ObjectSerializer<ItemMeta> {

    @Override
    public boolean supports(@NonNull Class<? super ItemMeta> type) {
        return ItemMeta.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull ItemMeta itemMeta, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (itemMeta.hasDisplayName()) {
            Component displayName = ComponentUtil.deserialize(itemMeta.getDisplayName());

            data.add("display-name", displayName, Component.class);
        }

        if (itemMeta.hasLore()) {
            List<Component> lore = ComponentUtil.deserialize(itemMeta.getLore());

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
    public ItemMeta deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        Optional<Component> displayNameOptional = Optional.ofNullable(data.get("display-name", Component.class));
        Optional<List<Component>> loreOptional = Optional.ofNullable(data.getAsList("lore", Component.class));

        Optional<Map<Enchantment, Integer>> enchantmentsOptional = Optional.ofNullable(data.getAsMap("enchantments", Enchantment.class, Integer.class));
        Optional<List<ItemFlag>> itemFlagsOptional = Optional.ofNullable(data.getAsList("item-flags", ItemFlag.class));

        ItemBuilder itemBuilder = ItemBuilder.from(Material.STONE);

        displayNameOptional.ifPresent(itemBuilder::name);
        loreOptional.ifPresent(itemBuilder::lore);

        enchantmentsOptional.ifPresent(itemBuilder::enchant);
        itemFlagsOptional.map(itemFlags -> itemFlags.toArray(new ItemFlag[0])).ifPresent(itemBuilder::flags);

        return itemBuilder.build().getItemMeta();
    }
}
