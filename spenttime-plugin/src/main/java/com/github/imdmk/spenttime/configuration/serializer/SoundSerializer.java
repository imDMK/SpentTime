package com.github.imdmk.spenttime.configuration.serializer;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class SoundSerializer implements ObjectSerializer<Sound> {

    @Override
    public boolean supports(@NotNull Class<? super Sound> type) {
        return Sound.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull Sound sound, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.setValue(sound.name(), String.class);
    }

    @Override
    public Sound deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        return Sound.valueOf(data.getValue(String.class));
    }
}
