package com.github.imdmk.playtime.platform.placeholder.adapter;

import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import org.jetbrains.annotations.NotNull;

/**
 * Strategy for registering {@link PluginPlaceholder} instances
 * against a concrete placeholder platform (e.g. PlaceholderAPI),
 * or acting as a no-op implementation when the platform is not present.
 */
public interface PlaceholderAdapter {

    /**
     * @return {@code true} if the underlying placeholder platform is available.
     */
    boolean isAvailable();

    /**
     * Registers the given placeholder if the platform is available.
     */
    void register(@NotNull PluginPlaceholder placeholder);

    /**
     * Unregisters the given placeholder, if it was registered.
     */
    void unregister(@NotNull PluginPlaceholder placeholder);

    /**
     * Unregisters all placeholders managed by this registrar.
     */
    void unregisterAll();
}

