package com.github.imdmk.playtime.platform.placeholder;

import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service
public final class PlaceholderService {

    private final PlaceholderRegistry registry;

    @Inject
    public PlaceholderService(
            @NotNull Plugin plugin,
            @NotNull PluginLogger logger
    ) {
        this.registry = PlaceholderRegistryFactory.create(plugin, logger);
    }

    public void register(@NotNull PluginPlaceholder placeholder) {
        registry.register(placeholder);
    }

    public void unregister(@NotNull PluginPlaceholder placeholder) {
        registry.unregister(placeholder);
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        registry.unregisterAll();
    }
}
