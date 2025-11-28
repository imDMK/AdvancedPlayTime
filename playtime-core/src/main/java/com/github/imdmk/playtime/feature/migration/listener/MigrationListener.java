package com.github.imdmk.playtime.feature.migration.listener;

import com.github.imdmk.playtime.feature.migration.MigrationResult;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Listener interface receiving callbacks about the lifecycle of a migration run.
 *
 * <p>Implementations may use these callbacks to provide progress logging,
 * UI updates, console output, or metrics tracking.</p>
 *
 * <p>All callbacks are invoked synchronously by the {@code MigrationRunner},
 * unless explicitly documented otherwise.</p>
 */
public interface MigrationListener {

    /**
     * Called once when the migration process begins.
     *
     * @param total total number of players scheduled for migration
     */
    default void onStart(int total) {}

    /**
     * Called when a single player has been migrated successfully.
     *
     * @param player the player that was successfully migrated
     */
    default void onSuccess(@NotNull OfflinePlayer player) {}

    /**
     * Called when migration of a player fails.
     *
     * @param player    the player for whom migration failed
     * @param throwable the error describing the reason for failure
     */
    default void onFailed(@NotNull OfflinePlayer player, @NotNull Throwable throwable) {}

    /**
     * Called once when the entire migration process has finished.
     *
     * @param result aggregated migration summary, including statistics and duration
     */
    default void onEnd(@NotNull MigrationResult result) {}
}
