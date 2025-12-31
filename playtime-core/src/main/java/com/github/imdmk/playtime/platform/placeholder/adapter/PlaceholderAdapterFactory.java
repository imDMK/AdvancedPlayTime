package com.github.imdmk.playtime.platform.placeholder.adapter;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderAdapterFactory {

    private static final String PLACEHOLDER_API_NAME = "PlaceholderAPI";

    public static PlaceholderAdapter createFor(
            @NotNull Plugin plugin,
            @NotNull Server server,
            @NotNull PluginLogger logger
    ) {
        final boolean isEnabled = server.getPluginManager().isPluginEnabled(PLACEHOLDER_API_NAME);
        if (isEnabled) {
            logger.info("PlaceholderAPI detected — using PlaceholderAdapterImpl.");
            return new PlaceholderAdapterImpl(plugin, logger);
        }

        logger.info("PlaceholderAPI not found — using NoopPlaceholderAdapter.");
        return new NoopPlaceholderAdapter();
    }

    private PlaceholderAdapterFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}

