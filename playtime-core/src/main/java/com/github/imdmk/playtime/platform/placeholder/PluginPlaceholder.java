package com.github.imdmk.playtime.platform.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Plugin-level abstraction for a single placeholder.
 * <p>
 * This interface is framework-agnostic and does not depend on PlaceholderAPI.
 * Implementations can be adapted to any placeholder platform.
 */
public interface PluginPlaceholder {

    /**
     * Unique identifier of the placeholder set.
     * Example: "playtime"
     */
    @NotNull String identifier();

    /**
     * Called for online players, if supported by the underlying platform.
     */
    default @Nullable String onRequest(@NotNull Player player, @NotNull String params) {
        return null;
    }

    /**
     * Called for offline players, if supported by the underlying platform.
     */
    default @Nullable String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        return null;
    }
}
