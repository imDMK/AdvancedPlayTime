package com.github.imdmk.playtime.feature.migration.listener;

import com.github.imdmk.playtime.feature.migration.MigrationResult;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Listener interface for receiving callbacks during a playtime data migration process.
 *
 * <p>This listener allows external components to observe and react to the
 * lifecycle of a migration operation – from start, through per-player
 * results, to the final completion summary.</p>
 *
 * <p>All methods are optional; implementations may override only the events
 * they are interested in.</p>
 */
public interface MigrationListener {

    /**
     * Called when the migration process begins.
     *
     * @param total total number of players scheduled for migration
     */
    default void onStart(int total) {}

    /**
     * Called when a player's data has been migrated successfully.
     *
     * @param player the offline player whose migration completed successfully
     */
    default void onSuccess(@NotNull OfflinePlayer player) {}

    /**
     * Called when a player's migration fails due to an unexpected error.
     *
     * @param player    the offline player whose migration failed
     * @param throwable the exception that caused the failure
     */
    default void onFailed(@NotNull OfflinePlayer player, @NotNull Throwable throwable) {}

    /**
     * Called when the migration process has completed for all players.
     *
     * @param result summary of the migration, including counts of successes, failures,
     *               and total processed players
     */
    default void onEnd(@NotNull MigrationResult result) {}
}
