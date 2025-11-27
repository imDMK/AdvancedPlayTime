package com.github.imdmk.playtime.shared.config;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.Validator;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central coordinator for creating, loading, and saving Okaeri-based configuration sections.
 *
 * <p>Provides:</p>
 * <ul>
 *   <li>Consistent YAML formatting via a preconfigured {@link YamlSnakeYamlConfigurer} (block style, 2-space indent, no line wrapping).</li>
 *   <li>Async I/O offloaded to a dedicated single-threaded executor (daemon) to avoid blocking the main thread.</li>
 *   <li>Bulk operations (create all, load all, save all) and type-based lookup of created configs.</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong> Internally uses concurrent collections ({@link ConcurrentHashMap} &amp; {@link java.util.concurrent.ConcurrentHashMap#newKeySet()})
 * and a single-thread executor for async tasks. Public methods that mutate state are not synchronized except where necessary; callers should prefer using
 * async methods for I/O. The executor is shut down by {@link #shutdown()}.</p>
 *
 * <p><strong>Lifecycle:</strong></p>
 * <pre>{@code
 * ConfigManager cm = new ConfigManager(dataFolder);
 * MyConfig cfg = cm.create(MyConfig.class);
 * cm.loadAll().join();
 * cm.saveAllSync();
 * cm.shutdown();
 * }</pre>
 *
 * @see ConfigSection
 * @see OkaeriConfig
 * @see YamlSnakeYamlConfigurer
 * @see ConfigLoadException
 */
public final class ConfigManager {

    private static final String THREAD_NAME = "playtime-config";

    /**
     * Creates a dedicated single-threaded daemon executor used for async config I/O.
     *
     * @return new single-threaded executor named {@code playtime-config}
     */
    private static ExecutorService newSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, THREAD_NAME);
            t.setDaemon(true);
            return t;
        });
    }

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, ConfigSection> byType = new ConcurrentHashMap<>();

    private final PluginLogger logger;
    private final ExecutorService executor;
    private final File dataFolder;

    public ConfigManager(@NotNull PluginLogger logger, @NotNull ExecutorService executor, @NotNull File dataFolder) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.executor = Validator.notNull(executor, "executor cannot be null");
        this.dataFolder = Validator.notNull(dataFolder, "dataFolder cannot be null");
    }

    public ConfigManager(@NotNull PluginLogger logger, @NotNull File dataFolder) {
        this(logger, newSingleThreadExecutor(), dataFolder);
    }

    /**
     * Creates a configuration instance of the given type, binds its file, applies YAML/serdes settings,
     * saves defaults if necessary, loads content, and registers it for bulk operations.
     *
     * <p>Requirements on the config class:</p>
     * <ul>
     *   <li>{@link ConfigSection#getFileName()} must return a non-blank relative path (e.g., {@code "config.yml"}).</li>
     *   <li>{@link ConfigSection#getSerdesPack()} must not return {@code null}.</li>
     * </ul>
     *
     * @param configClass config type to create
     * @param <T>         specific {@link ConfigSection} subtype
     * @return created and loaded config instance
     * @throws IllegalStateException if the file name is blank
     * @throws NullPointerException  if the serdes pack is {@code null}
     * @throws OkaeriException       if saving defaults or loading fails internally (propagated from {@link OkaeriConfig})
     */
    public <T extends ConfigSection> @NotNull T create(@NotNull Class<T> configClass) {
        final T config = eu.okaeri.configs.ConfigManager.create(configClass);

        final String fileName = config.getFileName();
        if (fileName.isBlank()) {
            throw new IllegalStateException(
                    "Missing config file name for " + configClass.getName()
                            + " – override getFileName() to return a non-empty path, e.g. 'config.yml'."
            );
        }

        final OkaeriSerdesPack serdes = Validator.notNull(config.getSerdesPack(), "config serdes pack cannot be null");

        final File file = new File(dataFolder, fileName);
        final YamlSnakeYamlConfigurer configurer = createYamlSnakeYamlConfigurer();

        config.withConfigurer(configurer, serdes);
        config.withSerdesPack(new SerdesCommons());
        config.withBindFile(file);
        config.withRemoveOrphans(true);
        config.saveDefaults();
        config.load(true);

        configs.add(config);
        byType.put(configClass, config);
        return config;
    }

    /**
     * Creates and registers all provided root config types in the specified order.
     *
     * @param configClasses ordered list of root {@link ConfigSection} classes
     * @throws OkaeriException if any config fails during creation or loading
     */
    public void createAll(@NotNull List<Class<? extends ConfigSection>> configClasses) {
        configClasses.forEach(this::create);
    }

    /**
     * Returns the config instance of the given type, or {@code null} if not created.
     *
     * @param type config class used as the lookup key
     * @param <T>  config subtype
     * @return config instance or {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T extends ConfigSection> T get(@NotNull Class<T> type) {
        return (T) byType.get(type);
    }

    /**
     * Returns the config instance of the given type, throwing if it was not created.
     *
     * @param type config class used as the lookup key
     * @param <T>  config subtype
     * @return non-null config instance
     * @throws IllegalStateException if a config of this type has not been created yet
     */
    public <T extends ConfigSection> @NotNull T require(@NotNull Class<T> type) {
        final T config = get(type);
        if (config == null) {
            throw new IllegalStateException("Config not created: " + type.getName());
        }
        return config;
    }

    /**
     * Creates a {@link YamlSnakeYamlConfigurer} with conservative and readable defaults:
     * block style, 2-space indentation, no line splitting, limited aliases, no recursive keys.
     *
     * @return configured YAML configurer
     */
    private @NotNull YamlSnakeYamlConfigurer createYamlSnakeYamlConfigurer() {
        final LoaderOptions loader = new LoaderOptions();
        loader.setAllowRecursiveKeys(false);
        loader.setMaxAliasesForCollections(50);

        final Constructor constructor = new Constructor(loader);

        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setSplitLines(false);

        final Representer representer = new ConfigRepresenter(options);
        final Resolver resolver = new Resolver();

        final Yaml yaml = new Yaml(constructor, representer, options, loader, resolver);
        return new YamlSnakeYamlConfigurer(yaml);
    }

    /**
     * Asynchronously reloads all registered configs on the manager's executor.
     *
     * @return future completed when all configs have been loaded, or completed exceptionally with {@link ConfigLoadException}
     */
    public @NotNull CompletableFuture<Void> loadAll() {
        return CompletableFuture.runAsync(() -> configs.forEach(this::load), executor);
    }

    /**
     * Synchronously reloads all registered configs on the caller thread.
     *
     * <p>Use this when you control the calling context and want deterministic reloading.</p>
     *
     * @throws ConfigLoadException if any config fails to load
     */
    public void loadAllSync() {
        configs.forEach(this::load);
    }

    /**
     * Loads the provided config and wraps {@link OkaeriException} into {@link ConfigLoadException} with logging.
     *
     * @param config target config to load
     * @throws ConfigLoadException on load failure
     */
    private void load(@NotNull ConfigSection config) {
        try {
            config.load(true);
        } catch (OkaeriException e) {
            logger.error(e, "Failed to load config: %s", config.getClass().getSimpleName());
            throw new ConfigLoadException(e);
        }
    }

    /**
     * Asynchronously saves all registered configs on the manager's executor.
     *
     * @return future completed when all configs have been saved, or completed exceptionally with {@link ConfigLoadException}
     */
    public @NotNull CompletableFuture<Void> saveAll() {
        return CompletableFuture.runAsync(() -> configs.forEach(this::save), executor);
    }

    /**
     * Synchronously saves all registered configs on the caller thread.
     *
     * @throws ConfigLoadException if any config fails to save
     */
    public void saveAllSync() {
        configs.forEach(this::save);
    }

    /**
     * Saves the provided config and wraps {@link OkaeriException} into {@link ConfigLoadException} with logging.
     *
     * @param config target config to save
     * @throws ConfigLoadException on save failure
     */
    private void save(@NotNull ConfigSection config) {
        try {
            config.save();
        } catch (OkaeriException e) {
            logger.error(e, "Failed to save config: %s", config.getClass().getSimpleName());
            throw new ConfigLoadException(e);
        }
    }

    /**
     * Returns an immutable snapshot view of all registered config instances.
     *
     * @return unmodifiable set of registered configs
     */
    public @NotNull @Unmodifiable Set<ConfigSection> getConfigs() {
        return Collections.unmodifiableSet(configs);
    }

    /**
     * Shuts down the manager by stopping the executor and clearing internal registries.
     *
     * <p>After calling this method, the manager cannot be reused safely.</p>
     */
    public void shutdown() {
        executor.shutdownNow();
        configs.clear();
        byType.clear();
    }
}
