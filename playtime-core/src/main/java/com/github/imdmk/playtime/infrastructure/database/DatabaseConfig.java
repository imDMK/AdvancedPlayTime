package com.github.imdmk.playtime.infrastructure.database;

import com.github.imdmk.playtime.config.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration for the database connection layer.
 * <p>
 * Supports both embedded (SQLite/H2) and server-based engines (MySQL, MariaDB, PostgreSQL, SQL Server).
 * Depending on {@link DatabaseMode}, only a subset of fields is used.
 * <p>
 * All unused fields for a given mode are safely ignored by the connector.
 */
public final class DatabaseConfig extends ConfigSection {

    @Comment({
            "#",
            "# Database engine used by the plugin.",
            "# Supported: SQLITE, MYSQL, MARIADB, POSTGRESQL, H2, SQL",
            "# Recommended for beginners: SQLITE",
            "# Recommended for production: MYSQL or MARIADB",
            "#"
    })
    public DatabaseMode databaseMode = DatabaseMode.SQLITE;

    @Comment({
            "#",
            "# SQLite/H2 database file name.",
            "# Only used for: SQLITE, H2.",
            "# Path: /<databaseFileName>",
            "#"
    })
    public String databaseFileName = "database.db";
    @Comment({
            "#",
            "# Hostname or IP of the SQL server.",
            "# Required for: MYSQL, MARIADB, POSTGRESQL, SQL.",
            "#"
    })
    public String databaseHostName = "localhost";

    @Comment({
            "#",
            "# Database/schema name.",
            "# Required for: MYSQL, MARIADB, POSTGRESQL, SQL.",
            "#"
    })
    public String databaseName = "database";

    @Comment({
            "#",
            "# SQL username.",
            "# Required for: MYSQL, MARIADB, POSTGRESQL, SQL.",
            "#"
    })
    public String databaseUserName = "root";

    @Comment({
            "#",
            "# SQL password.",
            "# Required for: MYSQL, MARIADB, POSTGRESQL, SQL.",
            "# SECURITY WARNING:",
            "#   Do NOT share this file publicly if it contains production credentials.",
            "#   Consider using environment variables or secrets when possible.",
            "#"
    })
    public String databasePassword = "ExamplePassword1101";

    @Comment({
            "#",
            "# SQL port number.",
            "# Default MySQL/MariaDB: 3306",
            "# PostgreSQL: 5432",
            "# SQL Server: 1433",
            "# Required for: MYSQL, MARIADB, POSTGRESQL, SQL.",
            "#"
    })
    public int port = 3306;

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String getFileName() {
        return "databaseConfig.yml";
    }
}
