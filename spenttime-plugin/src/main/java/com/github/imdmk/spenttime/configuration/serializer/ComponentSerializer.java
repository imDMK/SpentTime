package com.github.imdmk.spenttime.configuration.serializer;

import com.github.imdmk.spenttime.util.ComponentUtil;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ComponentSerializer implements ObjectSerializer<Component> {

    @Override
    public boolean supports(@NotNull Class<? super Component> type) {
        return Component.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull Component component, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.setValue(ComponentUtil.serialize(component));
    }

    @Override
    public Component deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        return ComponentUtil.text(data.getValue(String.class));
    }
}
