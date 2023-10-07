package com.github.imdmk.spenttime.configuration;

import com.github.imdmk.spenttime.configuration.serializer.ItemMetaSerializer;
import com.github.imdmk.spenttime.configuration.serializer.ItemStackSerializer;
import com.github.imdmk.spenttime.configuration.transformer.ComponentStringTransformer;
import com.github.imdmk.spenttime.notification.configuration.NotificationSerializer;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;

import java.io.File;

public class ConfigurationFactory {

    private ConfigurationFactory() {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    public static <T extends OkaeriConfig> T create(Class<T> config, File dataFolder) {
        T configFile = ConfigManager.create(config);

        configFile.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons());
        configFile.withSerdesPack(registry -> {
            registry.register(new ComponentStringTransformer());
            registry.register(new ItemMetaSerializer());
            registry.register(new ItemStackSerializer());
            registry.register(new NotificationSerializer());
        });

        configFile.withBindFile(dataFolder);
        configFile.withRemoveOrphans(true);
        configFile.saveDefaults();
        configFile.load(true);

        return configFile;
    }
}
