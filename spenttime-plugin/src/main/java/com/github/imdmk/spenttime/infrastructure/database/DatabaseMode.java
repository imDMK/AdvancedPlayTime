package com.github.imdmk.spenttime.infrastructure.database;

/**
 * Represents the supported database modes for data storage.
 * <p>
 * This enum is used to select between local (SQLite) or remote (MySQL) database providers.
 */
public enum DatabaseMode {

    /**
     * Local file-based database using SQLite.
     * <p>
     * Recommended for development or lightweight deployments.
     */
    SQLITE,

    /**
     * Remote relational database using MySQL.
     * <p>
     * Recommended for production environments requiring scalability.
     */
    MYSQL
}
