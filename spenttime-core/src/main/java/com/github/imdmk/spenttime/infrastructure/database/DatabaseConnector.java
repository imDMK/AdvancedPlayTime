package com.github.imdmk.spenttime.infrastructure.database;

import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.shared.Validator;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Level;

/**
 * Provides a unified connection management layer between HikariCP and ORMLite.
 *
 * <p>This class encapsulates database connectivity logic for both SQLite and MySQL,
 * managing {@link HikariDataSource} pooling, {@link ConnectionSource} creation,
 * and graceful shutdown. It ensures safe, idempotent lifecycle transitions
 * (connect → use → close) and performs input validation to prevent misuse.</p>
 *
 * <p><strong>Supported modes:</strong></p>
 * <ul>
 *   <li><b>SQLite</b> — single-file database with automatic folder creation and a minimal 2-thread pool.</li>
 *   <li><b>MySQL</b> — pooled remote connection with hardened JDBC options and sane defaults.</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong> All lifecycle operations ({@link #connect(File)} and {@link #close()})
 * are synchronized. Concurrent read access via {@link #getConnectionSource()} is safe.</p>
 *
 * <p><strong>Lifecycle:</strong></p>
 * <pre>
 * DatabaseConnector connector = new DatabaseConnector(config);
 * connector.connect(dataFolder);
 * ConnectionSource source = connector.getConnectionSource();
 * // use repositories
 * connector.close();
 * </pre>
 *
 * @see ConnectionSource
 * @see HikariDataSource
 * @see DatabaseConfig
 */
public final class DatabaseConnector {

    private static final String JDBC_DRIVER_SQLITE = "org.sqlite.JDBC";
    private static final String JDBC_DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";

    private final PluginLogger logger;
    private final DatabaseConfig config;

    private volatile HikariDataSource dataSource;
    private volatile ConnectionSource connectionSource;

    public DatabaseConnector(@NotNull PluginLogger logger, @NotNull DatabaseConfig config) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.config = Validator.notNull(config, "config cannot be null");
    }

    /**
     * Establishes a new database connection and initializes the internal connection pool.
     *
     * <p>If already connected, this method throws {@link IllegalStateException}.</p>
     * <p>For SQLite, the parent directory is created automatically if missing.
     * For MySQL, a preconfigured URL with hardened connection parameters is used.</p>
     *
     * @param dataFolder plugin data folder used for SQLite file placement
     * @throws SQLException          if JDBC initialization fails
     * @throws IllegalStateException if a connection is already active
     */
    public synchronized void connect(@NotNull File dataFolder) throws SQLException {
        if (this.dataSource != null || this.connectionSource != null) {
            throw new IllegalStateException("DatabaseConnector is already connected.");
        }

        final HikariDataSource dataSource = createHikariDataSource();

        final DatabaseMode mode = config.databaseMode;
        switch (mode) {
            case SQLITE -> {
                // Ensure parent directory exists for SQLite database file
                final Path dbPath = dataFolder.toPath().resolve(config.databaseFileName);
                try {
                    Files.createDirectories(dbPath.getParent());
                } catch (Exception e) {
                    closeQuietly(dataSource);
                    throw new IllegalStateException("Cannot create SQLite directory: " + dbPath.getParent(), e);
                }

                dataSource.setDriverClassName(JDBC_DRIVER_SQLITE);
                dataSource.setJdbcUrl("jdbc:sqlite:" + dbPath);
                dataSource.setMaximumPoolSize(2); // minimal concurrency for local file-based DB
                dataSource.setMinimumIdle(1);
            }
            case MYSQL -> {
                dataSource.setDriverClassName(JDBC_DRIVER_MYSQL);
                final String url = "jdbc:mysql://" + config.hostname + ":" + config.port + "/" + config.database
                        + "?useSSL=false"
                        + "&allowPublicKeyRetrieval=true"
                        + "&useUnicode=true"
                        + "&characterEncoding=utf8mb4"
                        + "&serverTimezone=UTC"
                        + "&connectionTimeZone=UTC"
                        + "&tcpKeepAlive=true"
                        + "&rewriteBatchedStatements=true"
                        + "&socketTimeout=15000";
                dataSource.setJdbcUrl(url);
            }
            default -> {
                closeQuietly(dataSource);
                throw new IllegalStateException("Unknown database mode: " + mode);
            }
        }

        try {
            final ConnectionSource source = new DataSourceConnectionSource(dataSource, dataSource.getJdbcUrl());
            this.dataSource = dataSource;
            this.connectionSource = source;
            logger.info("Connected to %s database.", mode);
        } catch (SQLException e) {
            logger.error(e, "Failed to connect to database");
            closeQuietly(dataSource);
            this.dataSource = null;
            this.connectionSource = null;
            throw e;
        }
    }

    /**
     * Closes the active database connection and shuts down the underlying Hikari connection pool.
     *
     * <p>Safe to call multiple times. Exceptions during close are logged but ignored.</p>
     */
    public synchronized void close() {
        if (this.connectionSource == null && this.dataSource == null) {
            logger.warn("DatabaseConnector#close() called, but not connected.");
            return;
        }

        try {
            if (this.connectionSource != null) {
                this.connectionSource.close();
            }
        } catch (Exception e) {
            logger.error(e, "Failed to close ConnectionSource");
        }

        closeQuietly(this.dataSource);

        this.connectionSource = null;
        this.dataSource = null;

        logger.info("Database connection closed successfully.");
    }

    /**
     * Returns whether the connector is currently connected.
     *
     * @return {@code true} if both the {@link ConnectionSource} and {@link HikariDataSource} are active
     */
    public boolean isConnected() {
        final HikariDataSource ds = this.dataSource;
        return this.connectionSource != null && ds != null && !ds.isClosed();
    }

    /**
     * Returns the current active {@link ConnectionSource}, or {@code null} if not connected.
     *
     * @return active ORMLite connection source, or {@code null} if disconnected
     */
    public @Nullable ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }

    /**
     * Creates and configures a new {@link HikariDataSource} with conservative defaults.
     *
     * <p>Includes:
     * <ul>
     *   <li>Low pool size limits (max 5, min 1).</li>
     *   <li>Prepared statement caching (useful for MySQL).</li>
     *   <li>Connection and idle timeouts with safe lifetimes.</li>
     * </ul></p>
     *
     * @return configured Hikari data source
     */
    private @NotNull HikariDataSource createHikariDataSource() {
        final HikariDataSource data = new HikariDataSource();
        data.setPoolName("spenttime-db-pool");
        data.setMaximumPoolSize(5);
        data.setMinimumIdle(1);
        data.setUsername(this.config.username);
        data.setPassword(this.config.password);

        // logger
        try {
            data.getParentLogger().setLevel(Level.SEVERE);
        } catch (SQLFeatureNotSupportedException ignored) {}

        // Prepared statement cache (effective for MySQL; harmless for SQLite)
        data.addDataSourceProperty("cachePrepStmts", true);
        data.addDataSourceProperty("prepStmtCacheSize", 250);
        data.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        data.addDataSourceProperty("useServerPrepStmts", true);

        // Timeout configuration (milliseconds)
        data.setConnectionTimeout(10_000);
        data.setIdleTimeout(60_000);
        data.setMaxLifetime(600_000);

        return data;
    }

    /**
     * Closes the given {@link HikariDataSource} without propagating exceptions.
     *
     * @param ds data source to close (nullable)
     */
    private static void closeQuietly(@Nullable HikariDataSource ds) {
        try {
            if (ds != null) {
                ds.close();
            }
        } catch (Exception ignored) {
        }
    }
}
