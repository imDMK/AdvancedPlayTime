package com.github.imdmk.spenttime.infrastructure.database;

/**
 * Represents the supported database modes for data storage.
 * <p>
 * This enum is used to select between local (SQLite) or remote (MySQL) database providers.
 */
public enum DatabaseMode {

    MYSQL,

    MARIADB,

    SQLITE,

    POSTGRESQL,

    H2,

    SQL

}
