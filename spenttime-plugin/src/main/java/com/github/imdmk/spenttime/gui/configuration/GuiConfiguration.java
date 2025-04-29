package com.github.imdmk.spenttime.gui.configuration;

import com.github.imdmk.spenttime.configuration.ConfigSection;
import com.github.imdmk.spenttime.configuration.serializer.ItemStackSerializer;
import com.github.imdmk.spenttime.configuration.transformer.ComponentTransformer;
import com.github.imdmk.spenttime.gui.configuration.item.ConfigurableGuiItemSerializer;
import com.github.imdmk.spenttime.gui.configuration.item.GuiItemConfiguration;
import com.github.imdmk.spenttime.user.gui.SpentTimeTopGuiConfiguration;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.yaml.bukkit.serdes.serializer.ItemMetaSerializer;

public class GuiConfiguration extends ConfigSection {

    public GuiItemConfiguration items = new GuiItemConfiguration();

    public SpentTimeTopGuiConfiguration spentTimeTop = new SpentTimeTopGuiConfiguration();

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new ConfigurableGuiItemSerializer());
            registry.register(new ItemStackSerializer());
            registry.register(new ItemMetaSerializer());
            registry.register(new ComponentTransformer());
        };
    }

    @Override
    public String getFileName() {
        return "guiConfiguration.yml";
    }
}
