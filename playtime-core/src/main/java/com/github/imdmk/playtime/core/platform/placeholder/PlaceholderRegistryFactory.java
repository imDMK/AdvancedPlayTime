package com.github.imdmk.playtime.core.platform.placeholder;

import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import org.bukkit.plugin.Plugin;

final class PlaceholderRegistryFactory {

    private static final String PAPI_PLUGIN_NAME = "PlaceholderAPI";

    PlaceholderRegistryFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    static PlaceholderRegistry create(
            Plugin plugin,
            PluginLogger logger
    ) {
        Plugin papi = plugin.getServer()
                .getPluginManager()
                .getPlugin(PAPI_PLUGIN_NAME);

        if (papi == null) {
            return new DisabledPlaceholderRegistry();
        }

        logger.info("Founded PlaceholderAPI plugin! Enabling integration...");
        return new PapiPlaceholderRegistry(plugin);
    }
}
