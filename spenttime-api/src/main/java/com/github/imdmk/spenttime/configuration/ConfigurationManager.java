package com.github.imdmk.spenttime.configuration;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfigurationManager {

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();

    private final Logger logger;
    private final ExecutorService executor;

    public ConfigurationManager(@NotNull Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.executor = Executors.newSingleThreadExecutor();
    }

    public <T extends ConfigSection> T create(@NotNull Class<T> config, @NotNull File dataFolder) {
        T configFile = ConfigManager.create(config);
        File file = new File(dataFolder, configFile.getFileName());

        YamlSnakeYamlConfigurer yamlSnakeYamlConfigurer = this.createYamlSnakeYamlConfigurer();

        configFile.withConfigurer(yamlSnakeYamlConfigurer);
        configFile.withSerdesPack(configFile.getSerdesPack());
        configFile.withBindFile(file);
        configFile.withRemoveOrphans(true);
        configFile.saveDefaults();
        configFile.load(true);

        this.configs.add(configFile);

        return configFile;
    }

    private @NotNull YamlSnakeYamlConfigurer createYamlSnakeYamlConfigurer() {
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

    public @NotNull CompletableFuture<Void> reloadAll() {
        return CompletableFuture.runAsync(this::loadAll, this.executor);
    }

    private void loadAll() {
        this.configs.forEach(this::load);
    }

    public void load(@NotNull OkaeriConfig config) {
        try {
            config.load(true);
        }
        catch (OkaeriException exception) {
            this.logger.log(Level.SEVERE, "Failed to load config: " + config.getClass().getSimpleName(), exception);
            throw new ConfigurationLoadException(exception);
        }
    }

    public void shutdown() {
        this.logger.info("Shutting down ConfigurationManager executor");
        this.executor.shutdownNow();
    }

}
