package com.github.imdmk.playtime.core.injector.processor.processors;

import com.github.imdmk.playtime.core.injector.annotations.PluginListener;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class PluginListenerProcessor implements ComponentProcessor<PluginListener> {

    private final Plugin plugin;

    public PluginListenerProcessor(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<PluginListener> annotation() {
        return PluginListener.class;
    }

    @Override
    public void process(Object instance, PluginListener annotation, ComponentProcessorContext context) {
        Listener listener = requireInstance(
                instance,
                Listener.class,
                PluginListener.class
        );

        plugin.getServer().getPluginManager()
                .registerEvents(listener, plugin);
    }
}
