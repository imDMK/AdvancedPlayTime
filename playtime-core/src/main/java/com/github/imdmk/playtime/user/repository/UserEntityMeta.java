package com.github.imdmk.playtime.user.repository;

import com.github.imdmk.playtime.database.repository.ormlite.EntityMeta;

/**
 * Database metadata for the {@code advanced_playtime_users} table.
 *
 * <p>This interface defines the table name and all column identifiers used by
 * {@link UserEntity} and the corresponding repository implementation.</p>
 *
 * <p>Centralizing these names ensures consistency across entity mappings,
 * DAO queries, migrations, and schema creation routines.</p>
 */
interface UserEntityMeta extends EntityMeta {

    /** Name of the table storing persistent user records. */
    String TABLE = "advanced_playtime_users";

    /**
     * Column name definitions for {@link UserEntity}.
     *
     * <p>All constants represent physical column names in the database schema.</p>
     */
    interface Col {

        /** Unique player identifier (primary key, NOT NULL). */
        String UUID = "uuid";

        /** Last known player name (NOT NULL, indexed). */
        String NAME = "name";

        /** Total accumulated playtime in milliseconds (NOT NULL). */
        String PLAYTIME_MILLIS = "playtimeMillis";
    }
}
