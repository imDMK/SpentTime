package com.github.imdmk.spenttime.configuration.transformer;

import com.github.imdmk.spenttime.util.ComponentUtil;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ComponentTransformer extends BidirectionalTransformer<Component, String> {

    @Override
    public GenericsPair<Component, String> getPair() {
        return this.genericsPair(Component.class, String.class);
    }

    @Override
    public String leftToRight(@NonNull Component component, @NonNull SerdesContext serdesContext) {
        return ComponentUtil.serialize(component);
    }

    @Override
    public Component rightToLeft(@NonNull String data, @NonNull SerdesContext serdesContext) {
        return ComponentUtil.deserialize(data);
    }
}
