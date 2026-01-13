package com.github.imdmk.playtime.platform.placeholder;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class PlaceholderRegistryFactory {

    private static final String PAPI_PLUGIN_NAME = "PlaceholderAPI";

    PlaceholderRegistryFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    static PlaceholderRegistry create(
            @NotNull Plugin plugin,
            @NotNull PluginLogger logger
    ) {
        final Plugin papi = plugin.getServer()
                .getPluginManager()
                .getPlugin(PAPI_PLUGIN_NAME);

        if (papi == null) {
            return new DisabledPlaceholderRegistry();
        }

        logger.info("Founded PlaceholderAPI plugin! Enabling integration...");
        return new PapiPlaceholderRegistry(plugin);
    }
}
