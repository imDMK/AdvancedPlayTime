package com.github.imdmk.spenttime.user.repository;

import com.github.imdmk.spenttime.infrastructure.database.repository.ormlite.EntityMeta;

/**
 * Database metadata for the {@code spent_time_users} table.
 *
 * <p>Defines the table name and all column identifiers used by the
 * {@link UserEntity} and its repository layer.</p>
 *
 * <p>This metadata provides a single source of truth for database field
 * names, ensuring consistency between entity mappings, queries, and migrations.</p>
 */
interface UserEntityMeta extends EntityMeta {

    /** Name of the table storing user records. */
    String TABLE = "spent_time_users";

    /**
     * Column name definitions for {@link UserEntity}.
     */
    interface Col {

        /** Unique player UUID (primary key). */
        String UUID = "uuid";

        /** Last known player name (indexed). */
        String NAME = "name";

        /** Total time spent by the player, in milliseconds. */
        String SPENT_MILLIS = "spentMillis";
    }
}
