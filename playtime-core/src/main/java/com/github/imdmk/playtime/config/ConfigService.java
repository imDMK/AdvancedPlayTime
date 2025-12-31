package com.github.imdmk.playtime.config;

import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.priority.Priority;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import eu.okaeri.configs.OkaeriConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service(priority = Priority.LOWEST)
public final class ConfigService {

    private final Set<OkaeriConfig> configs = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, OkaeriConfig> byType = new ConcurrentHashMap<>();

    private final File dataFolder;

    private final ConfigFactory factory;
    private final ConfigConfigurer configurer;
    private final ConfigLifecycle lifecycle;

    @Inject
    public ConfigService(@NotNull PluginLogger logger, @NotNull File dataFolder) {
        this.dataFolder = dataFolder;

        this.factory = new ConfigFactory();
        this.configurer = new ConfigConfigurer();
        this.lifecycle = new ConfigLifecycle(logger);
    }

    public <T extends OkaeriConfig> @NotNull T create(@NotNull Class<T> type, @NotNull String fileName) {
        final T config = factory.instantiate(type);
        final File file = new File(dataFolder, fileName);

        configurer.configure(config, file);
        lifecycle.initialize(config);

        register(type, config);
        return config;
    }

    @SuppressWarnings("unchecked")
    public <T extends OkaeriConfig> T get(@NotNull Class<T> type) {
        return (T) byType.get(type);
    }

    public <T extends OkaeriConfig> @NotNull T require(@NotNull Class<T> type) {
        final T config = get(type);
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

    @NotNull
    @Unmodifiable
    public Set<OkaeriConfig> getConfigs() {
        return Collections.unmodifiableSet(configs);
    }

    public void shutdown() {
        configs.clear();
        byType.clear();
    }

    private void register(Class<?> type, OkaeriConfig config) {
        configs.add(config);
        byType.put(type, config);
    }
}