package com.github.imdmk.spenttime.infrastructure.gui.configuration;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigGuiSerializer implements ObjectSerializer<ConfigGuiItem> {

    @Override
    public boolean supports(@NotNull Class<? super ConfigGuiItem> type) {
        return ConfigGuiItem.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull ConfigGuiItem item, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("material", item.material(), Material.class);
        data.add("name", item.name(), Component.class);
        data.addCollection("lore", item.lore(), Component.class);

        if (item.slot() > 0) {
            data.add("slot", item.slot(), Integer.class);
        }

        if (!item.enchantments().isEmpty()) {
            data.addAsMap("enchantments", item.enchantments(), Enchantment.class, Integer.class);
        }
    }

    @Override
    public ConfigGuiItem deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        Material material = data.get("material", Material.class);
        Component name = data.get("name", Component.class);
        List<Component> lore = data.getAsList("lore", Component.class);

        int slot = data.containsKey("slot") ?
                data.get("slot", Integer.class) : 0;

        Map<Enchantment, Integer> enchantments = data.containsKey("enchantments") ?
                data.getAsMap("enchantments", Enchantment.class, Integer.class) : new HashMap<>();

        return ConfigGuiItem.builder()
                .material(material)
                .nameComponent(name)
                .loreComponent(lore)
                .slot(slot)
                .enchantments(enchantments)
                .build();
    }
}
