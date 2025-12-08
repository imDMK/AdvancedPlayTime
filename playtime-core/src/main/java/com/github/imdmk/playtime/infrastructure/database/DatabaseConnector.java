package com.github.imdmk.playtime.infrastructure.database;

import com.github.imdmk.playtime.infrastructure.database.driver.configurer.DriverConfigurer;
import com.github.imdmk.playtime.infrastructure.database.driver.configurer.DriverConfigurerFactory;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.validate.Validator;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Level;

final class DatabaseConnector {

    private static final String POOL_NAME = "playtime-db-pool";

    private static final int DEFAULT_MAX_POOL_SIZE = 4;
    private static final int DEFAULT_MIN_IDLE = 0;

    private static final long DEFAULT_CONNECTION_TIMEOUT_MS = 10_000L;
    private static final long DEFAULT_IDLE_TIMEOUT_MS = 60_000L;
    private static final long DEFAULT_MAX_LIFETIME_MS = 600_000L;

    private static final boolean CACHE_PREP_STMTS = true;
    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;
    private static final boolean USE_SERVER_PREP_STMTS = true;

    private static final Level DATA_SOURCE_LOG_LEVEL = Level.SEVERE;

    private final PluginLogger logger;
    private final DatabaseConfig config;
    private final DriverConfigurer driverConfigurer;

    private volatile HikariDataSource dataSource;
    private volatile ConnectionSource connectionSource;

    /**
     * Creates a new connector with an explicit {@link DriverConfigurer}.
     * Useful for testing or advanced customization.
     *
     * @param logger           the plugin logger (never null)
     * @param config           the database configuration (never null)
     * @param driverConfigurer strategy used to configure the underlying JDBC driver (never null)
     */
    DatabaseConnector(
            @NotNull PluginLogger logger,
            @NotNull DatabaseConfig config,
            @NotNull DriverConfigurer driverConfigurer
    ) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.config = Validator.notNull(config, "config cannot be null");
        this.driverConfigurer = Validator.notNull(driverConfigurer, "driverConfigurer cannot be null");
    }

    /**
     * Creates a new connector using the default {@link DriverConfigurer}
     * resolved from {@link DriverConfigurerFactory} based on {@link DatabaseConfig#databaseMode}.
     *
     * @param logger the plugin logger (never null)
     * @param config the database configuration (never null)
     */
    DatabaseConnector(
            @NotNull PluginLogger logger,
            @NotNull DatabaseConfig config
    ) {
        this(logger, config, DriverConfigurerFactory.getFor(config.databaseMode));
    }

    /**
     * Establishes a new database connection and initializes the internal Hikari connection pool.
     * <p>
     * If already connected, this method throws {@link IllegalStateException}.
     * Engine-specific configuration (JDBC URL, file paths, flags) is delegated
     * to the configured {@link DriverConfigurer}.
     *
     * @param dataFolder plugin data folder, used especially for file-based databases (e.g. SQLite/H2)
     * @throws SQLException          if JDBC or ORMLite initialization fails
     * @throws IllegalStateException if a connection is already active
     */
    synchronized void connect(@NotNull File dataFolder) throws SQLException {
        Validator.notNull(dataFolder, "dataFolder cannot be null");

        if (dataSource != null || connectionSource != null) {
            throw new IllegalStateException("DatabaseConnector is already connected.");
        }

        final HikariDataSource ds = createHikariDataSource();

        try {
            // Delegated engine-specific configuration (JDBC URL, engine flags, filesystem prep)
            driverConfigurer.configure(ds, config, dataFolder);

            final String jdbcUrl = ds.getJdbcUrl();
            if (jdbcUrl == null || jdbcUrl.isBlank()) {
                throw new IllegalStateException("DriverConfigurer did not set JDBC URL for mode " + config.databaseMode);
            }

            final ConnectionSource source = new DataSourceConnectionSource(ds, jdbcUrl);

            dataSource = ds;
            connectionSource = source;

            logger.info("Connected to %s database.", config.databaseMode);
        } catch (SQLException e) {
            logger.error(e, "Failed to connect to database");
            closeQuietly(ds);
            dataSource = null;
            connectionSource = null;
            throw e;
        } catch (Exception e) {
            logger.error(e, "Failed to initialize database");
            closeQuietly(ds);
            dataSource = null;
            connectionSource = null;
            throw new IllegalStateException("Database initialization failed", e);
        }
    }

    /**
     * Closes the active database connection and shuts down the underlying HikariCP pool.
     * <p>
     * Safe to call multiple times. Exceptions during close are logged but ignored.
     */
    synchronized void close() {
        if (connectionSource == null && dataSource == null) {
            logger.warn("DatabaseConnector#close() called, but not connected.");
            return;
        }

        try {
            if (connectionSource != null) {
                connectionSource.close();
            }
        } catch (Exception e) {
            logger.error(e, "Failed to close ConnectionSource");
        }

        closeQuietly(dataSource);

        connectionSource = null;
        dataSource = null;

        logger.info("Database connection closed successfully.");
    }

    /**
     * Returns whether this connector is currently connected.
     *
     * @return {@code true} if both {@link ConnectionSource} and {@link HikariDataSource} are active
     */
    boolean isConnected() {
        final HikariDataSource ds = dataSource;
        return connectionSource != null && ds != null && !ds.isClosed();
    }

    /**
     * Returns the current active {@link ConnectionSource}, or {@code null} if not connected.
     *
     * @return active ORMLite connection source, or {@code null} if disconnected
     */
    @Nullable ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    /**
     * Creates and configures a new {@link HikariDataSource} with conservative, engine-agnostic defaults.
     * <p>
     * Includes:
     * <ul>
     *     <li>Moderate pool sizing (max 5, min 1 by default).</li>
     *     <li>Prepared statement caching (effective for MySQL-family drivers, harmless elsewhere).</li>
     *     <li>Connection, idle and lifetime timeouts with safe values.</li>
     * </ul>
     *
     * @return configured Hikari data source (not yet started)
     */
    private @NotNull HikariDataSource createHikariDataSource() {
        final HikariDataSource data = new HikariDataSource();
        data.setPoolName(POOL_NAME);

        data.setMaximumPoolSize(Math.max(DEFAULT_MAX_POOL_SIZE, Runtime.getRuntime().availableProcessors()));
        data.setMinimumIdle(DEFAULT_MIN_IDLE);

        data.setUsername(config.databaseUserName);
        data.setPassword(config.databasePassword);

        // Reduce noisy driver logging if supported
        try {
            data.getParentLogger().setLevel(DATA_SOURCE_LOG_LEVEL);
        } catch (SQLFeatureNotSupportedException ignored) {}

        // Prepared statement cache (useful for MySQL-family; harmless for others)
        data.addDataSourceProperty("cachePrepStmts", CACHE_PREP_STMTS);
        data.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        data.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        data.addDataSourceProperty("useServerPrepStmts", USE_SERVER_PREP_STMTS);

        // Timeout configuration (milliseconds)
        data.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT_MS);
        data.setIdleTimeout(DEFAULT_IDLE_TIMEOUT_MS);
        data.setMaxLifetime(DEFAULT_MAX_LIFETIME_MS);

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
        } catch (Exception ignored) {}
    }
}
