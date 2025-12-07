package com.github.imdmk.playtime.platform.placeholder.adapter;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.Validator;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Factory responsible for creating the appropriate {@link PlaceholderAdapter}
 * implementation based on runtime plugin availability.
 *
 * <p>This class detects whether <b>PlaceholderAPI</b> is installed and enabled on the server.
 * Depending on its presence, it returns either:</p>
 *
 * <ul>
 *     <li>{@link PlaceholderAPIAdapter} – full integration with PlaceholderAPI;</li>
 *     <li>{@link NoopPlaceholderAdapter} – a no-operation fallback that safely disables
 *     placeholder support without causing errors.</li>
 * </ul>
 *
 * <p>This allows the plugin to offer optional PlaceholderAPI integration without requiring it
 * as a hard dependency, while keeping all placeholder logic abstracted behind
 * the {@link PlaceholderAdapter} interface.</p>
 *
 * <p><strong>Thread-safety:</strong> The factory contains no mutable state and is fully thread-safe.</p>
 */
public final class PlaceholderAdapterFactory {

    private static final String PLACEHOLDER_API_NAME = "PlaceholderAPI";

    /**
     * Creates a {@link PlaceholderAdapter} appropriate for the current server environment.
     *
     * <p>If PlaceholderAPI is detected and enabled, a {@link PlaceholderAPIAdapter} is returned.
     * Otherwise, a {@link NoopPlaceholderAdapter} is provided, which safely performs no operations.</p>
     *
     * @param plugin the owning plugin instance; must not be null
     * @param server the Bukkit server instance; must not be null
     * @param logger the plugin logger for diagnostic output; must not be null
     * @return a fully initialized placeholder adapter suitable for the environment
     * @throws NullPointerException if any argument is null
     */
    public static PlaceholderAdapter createFor(
            @NotNull Plugin plugin,
            @NotNull Server server,
            @NotNull PluginLogger logger
    ) {
        Validator.notNull(plugin, "plugin cannot be null");
        Validator.notNull(server, "server cannot be null");
        Validator.notNull(logger, "logger cannot be null");

        boolean isEnabled = server.getPluginManager().isPluginEnabled(PLACEHOLDER_API_NAME);
        if (isEnabled) {
            logger.info("PlaceholderAPI detected — using PlaceholderApiAdapter.");
            return new PlaceholderAPIAdapter(plugin, logger);
        }

        logger.info("PlaceholderAPI not found — using NoopPlaceholderAdapter.");
        return new NoopPlaceholderAdapter();
    }

    private PlaceholderAdapterFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}

