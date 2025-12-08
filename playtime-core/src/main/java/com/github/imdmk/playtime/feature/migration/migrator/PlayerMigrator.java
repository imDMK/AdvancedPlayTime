package com.github.imdmk.playtime.feature.migration.migrator;

import com.github.imdmk.playtime.user.User;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Strategy interface responsible for migrating playtime data for a single
 * {@link OfflinePlayer} into the plugin’s internal {@link User} domain model.
 *
 * <p>Implementations of this interface define how legacy or external data sources
 * (e.g., Bukkit statistics API, flat files, third-party plugins, SQL tables)
 * are translated into the unified User format used by the PlayTime system.</p>
 *
 * <p><strong>Async contract:</strong><br>
 * The migration operation must be non-blocking and executed asynchronously.
 * All heavy computation and I/O must run off the main server thread.
 * The returned {@link CompletableFuture} represents the result of the migration.</p>
 *
 * <p>This interface is commonly used by bulk migration processes that iterate
 * through all stored players and invoke this migrator per user.</p>
 */
@FunctionalInterface
public interface PlayerMigrator {

    /**
     * Migrates playtime data for the given {@link OfflinePlayer}.
     *
     * @param player the offline player whose data should be migrated (never null)
     * @return a future completing with the migrated {@link User} instance,
     *         or completing exceptionally if the migration fails
     */
    CompletableFuture<User> migrate(@NotNull OfflinePlayer player);
}
