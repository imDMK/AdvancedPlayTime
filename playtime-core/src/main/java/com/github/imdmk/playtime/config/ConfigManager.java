package com.github.imdmk.playtime.config;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, ConfigSection> byType = new ConcurrentHashMap<>();

    private final File dataFolder;

    private final ConfigFactory factory;
    private final ConfigBinder binder;
    private final ConfigLifecycle lifecycle;

    public ConfigManager(@NotNull PluginLogger logger, @NotNull File dataFolder) {
        this.dataFolder = Validator.notNull(dataFolder, "dataFolder");

        this.factory = new ConfigFactory();
        this.binder = new ConfigBinder();
        this.lifecycle = new ConfigLifecycle(logger);
    }

    public <T extends ConfigSection> @NotNull T create(@NotNull Class<T> type) {
        final T config = factory.instantiate(type);
        final File file = new File(dataFolder, config.getFileName());

        binder.bind(config, file);
        lifecycle.initialize(config);

        register(type, config);
        return config;
    }

    public void createAll(@NotNull List<Class<? extends ConfigSection>> types) {
        types.forEach(this::create);
    }

    @SuppressWarnings("unchecked")
    public <T extends ConfigSection> T get(@NotNull Class<T> type) {
        return (T) byType.get(type);
    }

    public <T extends ConfigSection> @NotNull T require(@NotNull Class<T> type) {
        T config = get(type);

        if (config == null) {
            throw new IllegalStateException("Config not created: " + type.getName());
        }

        return config;
    }

    public void loadAll() {
        configs.forEach(lifecycle::load);
    }

    public void saveAll() {
        configs.forEach(lifecycle::save);
    }

    public @NotNull @Unmodifiable Set<ConfigSection> getConfigs() {
        return Collections.unmodifiableSet(configs);
    }

    public void clearAll() {
        configs.clear();
        byType.clear();
    }

    private void register(Class<?> type, ConfigSection config) {
        configs.add(config);
        byType.put(type, config);
    }
}