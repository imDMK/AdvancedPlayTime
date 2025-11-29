package com.github.imdmk.playtime.feature.migration.migrator;

import com.github.imdmk.playtime.user.User;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Functional abstraction responsible for migrating playtime data
 * for a single {@link OfflinePlayer} into the plugin’s {@link User}
 * domain model.
 *
 * <p>This interface is typically implemented by migration strategies
 * used during the initial setup or data import from legacy systems
 * (e.g., Bukkit statistics API, flat files, other plugins, external databases).</p>
 *
 * <p>The migration is asynchronous and must never block the server thread.
 * Implementations should perform I/O or heavy operations on background executors.</p>
 */
@FunctionalInterface
public interface PlayerMigrator {

    /**
     * Migrates playtime data for the given {@link OfflinePlayer}.
     *
     * @param player the offline player whose data should be migrated (never null)
     * @return a future completing with the migrated {@link User},
     *         or completing exceptionally if migration fails
     */
    CompletableFuture<User> migrate(@NotNull OfflinePlayer player);
}
