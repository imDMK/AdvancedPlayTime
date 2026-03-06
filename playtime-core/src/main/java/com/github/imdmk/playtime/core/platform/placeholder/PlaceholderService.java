package com.github.imdmk.playtime.core.platform.placeholder;

import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import org.bukkit.plugin.Plugin;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service
public final class PlaceholderService {

    private final PlaceholderRegistry registry;

    @Inject
    public PlaceholderService(
            Plugin plugin,
            PluginLogger logger
    ) {
        this.registry = PlaceholderRegistryFactory.create(plugin, logger);
    }

    public void register(PluginPlaceholder placeholder) {
        registry.register(placeholder);
    }

    public void unregister(PluginPlaceholder placeholder) {
        registry.unregister(placeholder);
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        registry.unregisterAll();
    }
}
