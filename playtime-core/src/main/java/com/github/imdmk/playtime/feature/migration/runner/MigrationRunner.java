package com.github.imdmk.playtime.feature.migration.runner;

import com.github.imdmk.playtime.feature.migration.listener.MigrationListener;

import java.util.List;

/**
 * Executes a migration process according to a specific strategy and
 * dispatches events to registered {@link MigrationListener} instances.
 *
 * <p>A {@code MigrationRunner} typically orchestrates:</p>
 * <ul>
 *     <li>retrieving players from a {@code PlayerProvider},</li>
 *     <li>delegating work to a {@code PlayerMigrator},</li>
 *     <li>collecting results and computing statistics,</li>
 *     <li>notifying listeners about progress.</li>
 * </ul>
 *
 * <p>Implementations define whether execution is synchronous or asynchronous,
 * and establish the threading model used for callbacks.</p>
 *
 * @param <T> the type returned upon completion (e.g. {@code MigrationResult})
 */
public interface MigrationRunner<T> {

    /**
     * Executes the migration process.
     *
     * @return a result object summarizing the completed migration
     */
    T execute();

    /**
     * Returns all listeners that will receive migration callbacks.
     *
     * @return an immutable or defensive-copied list of listeners
     */
    List<MigrationListener> listeners();
}
