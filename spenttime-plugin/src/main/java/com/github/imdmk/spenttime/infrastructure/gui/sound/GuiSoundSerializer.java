package com.github.imdmk.spenttime.infrastructure.gui.sound;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class GuiSoundSerializer implements ObjectSerializer<GuiSound> {

    @Override
    public boolean supports(@NotNull Class<? super GuiSound> type) {
        return GuiSound.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull GuiSound healingSound, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("sound", healingSound.sound(), Sound.class);
        data.add("volume", healingSound.volume(), float.class);
        data.add("pitch", healingSound.pitch(), float.class);
    }

    @Override
    public GuiSound deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        Sound sound = data.get("sound", Sound.class);
        float volume = data.get("volume", float.class);
        float pitch = data.get("pitch", float.class);

        return new GuiSound(sound, volume, pitch);
    }
}
