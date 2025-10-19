package com.github.imdmk.spenttime.config;

import com.github.imdmk.spenttime.util.Validator;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages loading, saving, and reloading of configuration sections.
 * <p>
 * Uses OkaeriConfig framework with customized YAML configuration.
 * Supports asynchronous reload of all configs and tracks created config instances.
 * </p>
 */
public final class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newSingleThreadExecutor();

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor;

    public ConfigManager(@NotNull ExecutorService executor) {
        this.executor = Validator.notNull(executor, "executor cannot be null");
    }

    public ConfigManager() {
        this(DEFAULT_EXECUTOR);
    }

    /**
     * Creates and loads a configuration section of the specified class type.
     * Config will be bound to a file named by the config's {@code getFileName()} method,
     * will use the config's specified serdes pack, and will remove orphaned entries.
     * Default values are saved if the file does not exist.
     *
     * @param <T>    the type of config section
     * @param configClass the config class to create, must not be null
     * @return the created and loaded configuration instance
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
     * Creates a custom YAML configurer used by the Okaeri config framework.
     * Configures YAML options such as indentation and flow style.
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
     * @throws ConfigLoadException if an error occurs during loading
     */
    public void load(@NotNull OkaeriConfig config) {
        try {
            config.load(true);
        }
        catch (OkaeriException exception) {
            LOGGER.error("Failed to load config: {}", config.getClass().getSimpleName(), exception);
            throw new ConfigLoadException(exception);
        }
    }


    /**
     * Saves all currently tracked configuration sections synchronously.
     */
    public void saveAll() {
        this.configs.forEach(this::save);
    }

    /**
     * Saves the specified configuration section from its bound file.
     * If saving fails, logs the error and throws a runtime exception.
     *
     * @param config the configuration instance to save, must not be null
     * @throws ConfigLoadException if an error occurs during saving
     */
    public void save(@NotNull OkaeriConfig config) {
        try {
            config.save();
        }
        catch (OkaeriException exception) {
            LOGGER.error("Failed to load config: {}", config.getClass().getSimpleName(), exception);
            throw new ConfigLoadException(exception);
        }
    }

    /**
     * Shuts down the internal executor service used for async operations.
     * Should be called on plugin shutdown to release resources.
     */
    public void shutdown() {
        LOGGER.info("Shutting down ConfigurationManager executor");
        this.executor.shutdownNow();
    }

}
