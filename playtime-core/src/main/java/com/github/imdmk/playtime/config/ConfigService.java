package com.github.imdmk.playtime.config;

import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service(priority = ComponentPriority.LOWEST, order = 0)
public final class ConfigService {

    private final Set<ConfigSection> configs = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, ConfigSection> byType = new ConcurrentHashMap<>();

    private final File dataFolder;

    private final ConfigFactory factory;
    private final ConfigConfigurer configurer;
    private final ConfigLifecycle lifecycle;

    @Inject
    public ConfigService(@NotNull File dataFolder) {
        this.dataFolder = dataFolder;

        this.factory = new ConfigFactory();
        this.configurer = new ConfigConfigurer();
        this.lifecycle = new ConfigLifecycle();
    }

    public <C extends ConfigSection> C create(@NotNull Class<C> type) {
        final C config = factory.instantiate(type);
        final File file = new File(dataFolder, config.fileName());

        configurer.configure(config, file, config.serdesPack());
        lifecycle.initialize(config);

        register(type, config);
        return config;
    }

    @SuppressWarnings("unchecked")
    public <C extends ConfigSection> C get(@NotNull Class<C> type) {
        return (C) byType.get(type);
    }

    public <C extends ConfigSection> C require(@NotNull Class<C> type) {
        final C config = get(type);
        if (config == null) {
            throw new ConfigAccessException("Config not created: " + type.getName());
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
    public Set<ConfigSection> getConfigs() {
        return Collections.unmodifiableSet(configs);
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        configs.clear();
        byType.clear();
    }

    private void register(Class<?> type, ConfigSection config) {
        configs.add(config);
        byType.put(type, config);
    }
}