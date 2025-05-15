package com.github.imdmk.spenttime.configuration.serializer;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ItemStackSerializer implements ObjectSerializer<ItemStack> {

    @Override
    public boolean supports(@NotNull Class<? super ItemStack> type) {
        return ItemStack.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull ItemStack itemStack, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("material", itemStack.getType(), Material.class);

        if (itemStack.getAmount() > 1) {
            data.add("amount", itemStack.getAmount(), Integer.class);
        }

        if (itemStack instanceof Damageable damageable) {
            if (damageable.getDamage() > 0) {
                data.add("durability", damageable.getDamage(), Short.class);
            }
        }

        if (itemStack.hasItemMeta()) {
            data.add("item-meta", itemStack.getItemMeta(), ItemMeta.class);
        }
    }

    @Override
    public ItemStack deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        Material material = data.get("material", Material.class);

        int amount = Optional.ofNullable(data.get("amount", Integer.class)).orElse(1);
        short durability = Optional.ofNullable(data.get("durability", Short.class)).orElse((short) 0);

        ItemStack itemStack = new ItemStack(material, amount);

        Optional.ofNullable(data.get("item-meta", ItemMeta.class))
                .ifPresent(itemStack::setItemMeta);

        if (itemStack.getItemMeta() instanceof Damageable damageable) {
            damageable.setDamage(durability);
            itemStack.setItemMeta(damageable);
        }

        return itemStack;
    }
}
