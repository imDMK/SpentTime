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

/**
 * Manages loading, saving, and reloading of configuration sections.
 * <p>
 * Uses OkaeriConfig framework with customized YAML configuration.
 * Supports asynchronous reload of all configs and tracks created config instances.
 * </p>
 */
public final class ConfigurationManager {

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();

    private final Logger logger;
    private final File dataFolder;
    private final ExecutorService executor;

    public ConfigurationManager(@NotNull Logger logger, @NotNull File dataFolder) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.dataFolder = Objects.requireNonNull(dataFolder, "dataFolder cannot be null");
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Creates and loads a configuration section of the specified class type.
     * Config will be bound to a file named by the config's {@code getFileName()} method,
     * will use the config's specified serdes pack, and will remove orphaned entries.
     * Default values are saved if the file does not exist.
     *
     * @param <T>    the type of config section
     * @param config the config class to create, must not be null
     * @return the created and loaded configuration instance
     */
    public <T extends ConfigSection> T create(@NotNull Class<T> config) {
        T configFile = ConfigManager.create(config);
        String fileName = configFile.getFileName();

        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("config file name cannot be empty");
        }

        File file = new File(this.dataFolder, fileName);

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

    /**
     * Creates a custom YAML configurer used by the Okaeri config framework.
     * Configures YAML options such as indentation and flow style.
     *
     * @return a configured {@link YamlSnakeYamlConfigurer} instance
     */
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

    /**
     * Asynchronously reloads all managed configuration sections.
     * Reload is executed in a single-threaded executor to avoid concurrency issues.
     *
     * @return a CompletableFuture representing the reload task
     */
    public @NotNull CompletableFuture<Void> reloadAll() {
        return CompletableFuture.runAsync(this::loadAll, this.executor);
    }

    /**
     * Loads all currently tracked configuration sections synchronously.
     */
    private void loadAll() {
        this.configs.forEach(this::load);
    }

    /**
     * Loads the specified configuration section from its bound file.
     * If loading fails, logs the error and throws a runtime exception.
     *
     * @param config the configuration instance to load, must not be null
     * @throws ConfigurationLoadException if an error occurs during loading
     */
    public void load(@NotNull OkaeriConfig config) {
        try {
            config.load(true);
        }
        catch (OkaeriException exception) {
            this.logger.log(Level.SEVERE, "Failed to load config: " + config.getClass().getSimpleName(), exception);
            throw new ConfigurationLoadException(exception);
        }
    }

    /**
     * Shuts down the internal executor service used for async operations.
     * Should be called on plugin shutdown to release resources.
     */
    public void shutdown() {
        this.logger.info("Shutting down ConfigurationManager executor");
        this.executor.shutdownNow();
    }

}

