package com.github.imdmk.playtime.database;

import com.github.imdmk.playtime.database.configurer.DataSourceConfigurer;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;

final class DataSourceConnector {

    private final PluginLogger logger;

    private final DataSourceFactory dataSourceFactory;
    private final DataSourceConfigurer dataSourceConfigurer;

    private volatile HikariDataSource dataSource;
    private volatile ConnectionSource connectionSource;

    DataSourceConnector(
            @NotNull PluginLogger logger,
            @NotNull DataSourceFactory dataSourceFactory,
            @NotNull DataSourceConfigurer dataSourceConfigurer
    ) {
        this.logger = logger;
        this.dataSourceFactory = dataSourceFactory;
        this.dataSourceConfigurer = dataSourceConfigurer;
    }

    synchronized void connect(@NotNull DatabaseConfig config, @NotNull File dataFolder) throws SQLException {
        if (dataSource != null || connectionSource != null) {
            throw new IllegalStateException("DataSource is already connected");
        }

        final HikariDataSource dataSource = dataSourceFactory.create(config);

        dataSourceConfigurer.configure(dataSource, config, dataFolder);
        if (dataSource.getJdbcUrl() == null) {
            throw new IllegalStateException("JDBC URL was not set by DataSourceConfigurer");
        }

        final ConnectionSource connectionSource = new DataSourceConnectionSource(dataSource, dataSource.getJdbcUrl());

        this.dataSource = dataSource;
        this.connectionSource = connectionSource;

        logger.info("Connected to %s database.", config.databaseMode);
    }

    synchronized void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception ignored) {}
        }

        if (dataSource != null) {
            dataSource.close();
        }

        connectionSource = null;
        dataSource = null;
    }

    @Nullable
    ConnectionSource getConnectionSource() {
        return connectionSource;
    }
}

