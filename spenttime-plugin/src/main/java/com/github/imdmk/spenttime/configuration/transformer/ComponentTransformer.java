package com.github.imdmk.spenttime.configuration.transformer;

import com.github.imdmk.spenttime.util.ComponentUtil;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ComponentTransformer extends BidirectionalTransformer<Component, String> {

    @Override
    public GenericsPair<Component, String> getPair() {
        return this.genericsPair(Component.class, String.class);
    }

    @Override
    public String leftToRight(@NotNull Component component, @NotNull SerdesContext serdesContext) {
        return ComponentUtil.serialize(component);
    }

    @Override
    public Component rightToLeft(@NotNull String data, @NotNull SerdesContext serdesContext) {
        return ComponentUtil.text(data);
    }
}
