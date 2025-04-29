package com.github.imdmk.spenttime.configuration;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ConfigurationManager {

    private final Set<ConfigSection> configs = new HashSet<>();

    public <T extends ConfigSection> T create(Class<T> config, File dataFolder) {
        T configFile = ConfigManager.create(config);

        YamlSnakeYamlConfigurer yamlSnakeYamlConfigurer = this.createYamlSnakeYamlConfigurer();

        configFile.withConfigurer(yamlSnakeYamlConfigurer);
        configFile.withSerdesPack(configFile.getSerdesPack());
        configFile.withBindFile(new File(dataFolder, configFile.getFileName()));
        configFile.withRemoveOrphans(true);
        configFile.saveDefaults();
        configFile.load(true);

        this.configs.add(configFile);

        return configFile;
    }

    private YamlSnakeYamlConfigurer createYamlSnakeYamlConfigurer() {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(loaderOptions);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
        dumperOptions.setIndent(2);
        dumperOptions.setSplitLines(false);

        Representer representer = new CustomRepresenter(dumperOptions);
        Resolver resolver = new Resolver();

        Yaml yaml = new Yaml(constructor, representer, dumperOptions, loaderOptions, resolver);
        return new YamlSnakeYamlConfigurer(yaml);
    }

    public CompletableFuture<Void> reloadAll() {
        return CompletableFuture.runAsync(this::loadAll);
    }

    private void loadAll() {
        for (ConfigSection config : this.configs) {
            try {
                config.load(true);
            }
            catch (OkaeriException exception) {
                throw new ConfigurationLoadException(exception);
            }
        }
    }
}
