package com.github.imdmk.spenttime.placeholder;

import com.github.imdmk.spenttime.placeholder.implementation.SpentTimeFormattedPlaceholder;
import com.github.imdmk.spenttime.placeholder.implementation.SpentTimePlaceholder;
import org.bukkit.plugin.PluginDescriptionFile;

public class PlaceholderRegistry {

    private final SpentTimeFormattedPlaceholder spentTimeFormattedPlaceholder;
    private final SpentTimePlaceholder spentTimePlaceholder;

    public PlaceholderRegistry(PluginDescriptionFile pluginDescriptionFile) {
        this.spentTimeFormattedPlaceholder = new SpentTimeFormattedPlaceholder(pluginDescriptionFile);
        this.spentTimePlaceholder = new SpentTimePlaceholder(pluginDescriptionFile);
    }

    public void registerAll() {
        this.spentTimeFormattedPlaceholder.register();
        this.spentTimePlaceholder.register();
    }

    public void unregisterAll() {
        this.spentTimeFormattedPlaceholder.unregister();
        this.spentTimePlaceholder.unregister();
    }
}
