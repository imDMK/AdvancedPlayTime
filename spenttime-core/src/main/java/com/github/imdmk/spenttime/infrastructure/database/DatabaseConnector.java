package com.github.imdmk.spenttime.infrastructure.database;

import com.github.imdmk.spenttime.shared.Validator;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * Database connection manager backed by HikariCP and ORMLite's {@link ConnectionSource}.
 * <p>
 * Supports SQLite and MySQL. Provides a small, safe connection pool configuration and a
 * predictable lifecycle (connect → use → close). The service is single-instance and
 * thread-safe around connect/close boundaries.
 */
public final class DatabaseConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnector.class);

    private static final String JDBC_DRIVER_SQLITE = "org.sqlite.JDBC";
    private static final String JDBC_DRIVER_MYSQL  = "com.mysql.cj.jdbc.Driver";

    private final DatabaseConfig config;

    private volatile HikariDataSource dataSource;
    private volatile ConnectionSource connectionSource;

    public DatabaseConnector(@NotNull DatabaseConfig config) {
        this.config = Validator.notNull(config, "config cannot be null");
    }

    /**
     * Establishes a pooled JDBC connection and creates the ORMLite {@link ConnectionSource}.
     * <p>
     * For SQLite, the database directory is created if missing.
     * For MySQL, a hardened URL with sane defaults is used.
     *
     * @throws SQLException          if opening the JDBC connection fails
     * @throws IllegalStateException if already connected
     */
    public synchronized void connect(@NotNull File dataFolder) throws SQLException {
        if (this.dataSource != null || this.connectionSource != null) {
            throw new IllegalStateException("DatabaseConnector is already connected.");
        }

        final HikariDataSource ds = createHikariDataSource();

        final DatabaseMode mode = this.config.databaseMode;
        switch (mode) {
            case SQLITE -> {
                // Use absolute path and ensure parent directories exist
                final Path dbPath = dataFolder.toPath().resolve(this.config.databaseFileName);
                try {
                    Files.createDirectories(dbPath.getParent());
                } catch (Exception e) {
                    closeQuietly(ds);
                    throw new IllegalStateException("Cannot create SQLite directory: " + dbPath.getParent(), e);
                }

                ds.setDriverClassName(JDBC_DRIVER_SQLITE);
                ds.setJdbcUrl("jdbc:sqlite:" + dbPath);
                ds.setMaximumPoolSize(2); // tiny pool
                ds.setMinimumIdle(1);
            }
            case MYSQL -> {
                ds.setDriverClassName(JDBC_DRIVER_MYSQL);
                final String url = "jdbc:mysql://" + this.config.hostname + ":" + this.config.port + "/" + this.config.database
                        + "?useSSL=false"
                        + "&allowPublicKeyRetrieval=true"
                        + "&useUnicode=true"
                        + "&characterEncoding=utf8mb4"
                        + "&serverTimezone=UTC"
                        + "&connectionTimeZone=UTC"
                        + "&tcpKeepAlive=true"
                        + "&rewriteBatchedStatements=true"
                        + "&socketTimeout=15000";
                ds.setJdbcUrl(url);
                // Pool size configured in createHikariDataSource()
            }
            default -> {
                closeQuietly(ds);
                throw new IllegalStateException("Unknown database mode: " + mode);
            }
        }

        try {
            final ConnectionSource source = new DataSourceConnectionSource(ds, ds.getJdbcUrl());
            this.dataSource = ds;
            this.connectionSource = source;
            LOGGER.info("Connected to {} database.", mode);
        } catch (SQLException e) {
            LOGGER.error("Failed to connect to database", e);
            closeQuietly(ds);
            this.dataSource = null;
            this.connectionSource = null;
            throw e;
        }
    }

    /**
     * Closes ORMLite {@link ConnectionSource} and the underlying Hikari {@link HikariDataSource}.
     * Safe to call multiple times.
     */
    public synchronized void close() {
        if (this.connectionSource == null && this.dataSource == null) {
            LOGGER.warn("DatabaseConnector#close() called, but not connected.");
            return;
        }

        try {
            if (this.connectionSource != null) {
                this.connectionSource.close();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to close ConnectionSource", e);
        }

        closeQuietly(this.dataSource);

        this.connectionSource = null;
        this.dataSource = null;

        LOGGER.info("Database connection closed successfully.");
    }

    /** @return {@code true} if the connector is open and the datasource is not closed. */
    public boolean isConnected() {
        final HikariDataSource ds = this.dataSource;
        return this.connectionSource != null && ds != null && !ds.isClosed();
    }

    /** @return active {@link ConnectionSource}, or {@code null} if not connected. */
    public @Nullable ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }

    /**
     * Creates a conservative HikariCP datasource with safe timeouts and statement caching.
     */
    private @NotNull HikariDataSource createHikariDataSource() {
        final HikariDataSource ds = new HikariDataSource();
        ds.setPoolName("spenttime-db-pool");
        ds.setMaximumPoolSize(5);
        ds.setMinimumIdle(1);
        ds.setUsername(this.config.username);
        ds.setPassword(this.config.password);

        // Prepared statement cache (effective for MySQL; harmless elsewhere)
        ds.addDataSourceProperty("cachePrepStmts", true);
        ds.addDataSourceProperty("prepStmtCacheSize", 250);
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        ds.addDataSourceProperty("useServerPrepStmts", true);

        // Timeouts (ms)
        ds.setConnectionTimeout(10_000);
        ds.setIdleTimeout(60_000);
        ds.setMaxLifetime(600_000);

        return ds;
    }

    private static void closeQuietly(@Nullable HikariDataSource ds) {
        try {
            if (ds != null) ds.close();
        } catch (Exception ignored) { }
    }
}
