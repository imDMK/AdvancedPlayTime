package com.github.imdmk.playtime.feature.migration;

import java.time.Duration;

/**
 * Immutable summary of a completed migration run.
 *
 * <p>This record captures aggregated statistics such as:</p>
 * <ul>
 *     <li>total players processed,</li>
 *     <li>successful migrations,</li>
 *     <li>failed migrations,</li>
 *     <li>total elapsed time.</li>
 * </ul>
 *
 * <p>Instances of this record are typically created by a
 * {@code MigrationRunner} implementation after completing the migration workflow.</p>
 *
 * @param total      total number of players considered
 * @param successful number of successful migrations
 * @param failed     number of failed migrations
 * @param took       total duration of the migration process
 */
public record MigrationResult(int total, int successful, int failed, Duration took) {

    /**
     * Returns an empty result representing a migration that processed
     * no players and took zero time.
     *
     * @return a zero-valued {@code MigrationResult}
     */
    public static MigrationResult empty() {
        return new MigrationResult(0, 0, 0, Duration.ZERO);
    }
}
