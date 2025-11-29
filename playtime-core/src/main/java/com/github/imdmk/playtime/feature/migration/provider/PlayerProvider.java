package com.github.imdmk.playtime.feature.migration.provider;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Provides access to all players that should participate in a migration run.
 *
 * <p>Typical implementations include:
 * <ul>
 *     <li>fetching all known {@link OfflinePlayer} instances from Bukkit,</li>
 *     <li>loading players from external storage,</li>
 *     <li>filtering players based on custom eligibility rules.</li>
 * </ul>
 *
 * <p>The contract guarantees that the returned collection is non-null,
 * but may be empty.</p>
 */
public interface PlayerProvider {

    /**
     * Returns a collection of all players eligible for migration.
     *
     * @return a non-null collection containing zero or more {@link OfflinePlayer} instances
     */
    @NotNull Collection<OfflinePlayer> getAllPlayers();
}
