package com.github.imdmk.spenttime.configuration.serializer.pack;

import com.github.imdmk.spenttime.configuration.serializer.ComponentSerializer;
import com.github.imdmk.spenttime.configuration.serializer.ItemMetaSerializer;
import com.github.imdmk.spenttime.configuration.serializer.ItemStackSerializer;
import com.github.imdmk.spenttime.configuration.serializer.NotificationSerializer;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SpentTimePack implements OkaeriSerdesPack {

    @Override
    public void register(@NonNull SerdesRegistry registry) {
        registry.register(new ComponentSerializer());
        registry.register(new ItemMetaSerializer());
        registry.register(new ItemStackSerializer());
        registry.register(new NotificationSerializer());
    }
}
