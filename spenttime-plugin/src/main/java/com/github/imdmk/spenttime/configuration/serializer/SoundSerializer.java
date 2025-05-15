package com.github.imdmk.spenttime.configuration.serializer;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class SoundSerializer implements ObjectSerializer<Sound> {

    @Override
    public boolean supports(@NotNull Class<? super Sound> type) {
        return Sound.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull Sound sound, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.setValue(sound.getKeyOrThrow().getKey(), String.class);
    }

    @Override
    public Sound deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        return Registry.SOUNDS.get(NamespacedKey.minecraft(data.getValue(String.class)));
    }
}
