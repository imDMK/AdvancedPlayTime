package com.github.imdmk.spenttime.shared.config;

import com.github.imdmk.spenttime.shared.Validator;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central configuration manager for the application.
 * <p>
 * Handles creation, loading, saving, and reloading of configuration sections
 * using the OkaeriConfig framework with a customized SnakeYAML configurer.
 * Maintains a thread-safe registry of active configuration instances.
 * </p>
 */
public final class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newSingleThreadExecutor();

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor;

    /**
     * Creates a new {@link ConfigManager} using the specified executor service.
     *
     * @param executor executor used for async config operations
     */
    public ConfigManager(@NotNull ExecutorService executor) {
        this.executor = Validator.notNull(executor, "executor cannot be null");
    }

    /**
     * Creates a {@link ConfigManager} using a default single-thread executor.
     */
    public ConfigManager() {
        this(DEFAULT_EXECUTOR);
    }

    /**
     * Creates and loads a configuration section of the specified class type.
     * <p>
     * The config is automatically bound to its target file, initializes
     * default values if missing, and registers itself for future reload/save.
     * </p>
     *
     * @param <T>         the type of config section
     * @param configClass the config class to create, must not be null
     * @return the loaded configuration instance
     * @throws IllegalArgumentException if config file name is empty
     */
    public <T extends ConfigSection> T create(@NotNull Class<T> configClass) {
        T config = eu.okaeri.configs.ConfigManager.create(configClass);

        String fileName = config.getFileName();
        if (fileName.isBlank()) {
            throw new IllegalArgumentException("config file name cannot be empty");
        }

        YamlSnakeYamlConfigurer configurer = this.createYamlSnakeYamlConfigurer();

        config.withConfigurer(configurer);
        config.withSerdesPack(config.getSerdesPack());
        config.withBindFile(fileName);
        config.withRemoveOrphans(true);
        config.saveDefaults();
        config.load(true);

        this.configs.add(config);

        return config;
    }

    /**
     * Builds and returns a custom YAML configurer for Okaeri.
     * <p>
     * Adjusts indentation, line splitting, and flow style to ensure
     * consistent formatting across all YAML config files.
     * </p>
     *
     * @return a configured {@link YamlSnakeYamlConfigurer} instance
     */
    private @NotNull YamlSnakeYamlConfigurer createYamlSnakeYamlConfigurer() {
        LoaderOptions loader = new LoaderOptions();
        Constructor constructor = new Constructor(loader);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
        options.setIndent(2);
        options.setSplitLines(false);

        Representer representer = new CustomRepresenter(options);
        Resolver resolver = new Resolver();

        Yaml yaml = new Yaml(constructor, representer, options, loader, resolver);
        return new YamlSnakeYamlConfigurer(yaml);
    }

    /**
     * Reloads all registered configuration files synchronously.
     * Logs errors if any config fails to load.
     */
    private void loadAll() {
        this.configs.forEach(this::load);
    }

    /**
     * Loads a specific configuration section from its bound file.
     *
     * @param config the configuration instance to load
     * @throws ConfigLoadException if loading fails
     */
    public void load(@NotNull OkaeriConfig config) {
        try {
            config.load(true);
        } catch (OkaeriException exception) {
            LOGGER.error("Failed to load config: {}", config.getClass().getSimpleName(), exception);
            throw new ConfigLoadException(exception);
        }
    }

    /**
     * Saves all registered configuration sections synchronously.
     * Logs and continues on individual save failures.
     */
    public void saveAll() {
        this.configs.forEach(this::save);
    }

    /**
     * Saves a specific configuration section to its bound file.
     *
     * @param config the configuration instance to save
     * @throws ConfigLoadException if saving fails
     */
    public void save(@NotNull OkaeriConfig config) {
        try {
            config.save();
        } catch (OkaeriException exception) {
            LOGGER.error("Failed to save config: {}", config.getClass().getSimpleName(), exception);
            throw new ConfigLoadException(exception);
        }
    }

    /**
     * Returns an unmodifiable view of all configuration instances
     * that have been registered by this manager.
     *
     * @return an unmodifiable {@link Set} containing all registered {@link OkaeriConfig} instances
     */
    @NotNull
    @Unmodifiable
    public Set<OkaeriConfig> getConfigs() {
        return Collections.unmodifiableSet(this.configs);
    }

    /**
     * Gracefully shuts down the internal executor service.
     * Should be invoked on application shutdown to release threads.
     */
    public void shutdown() {
        LOGGER.info("Shutting down ConfigurationManager executor");
        this.executor.shutdownNow();
    }

}
