package com.github.imdmk.playtime.core.database;

import com.github.imdmk.playtime.core.database.configurer.DataSourceConfigurer;
import com.github.imdmk.playtime.core.database.configurer.DataSourceConfigurerFactory;
import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariDataSource;
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
            PluginLogger logger,
            DataSourceFactory dataSourceFactory,
            DatabaseConfig config
    ) {
        this.logger = logger;
        this.dataSourceFactory = dataSourceFactory;
        this.dataSourceConfigurer = DataSourceConfigurerFactory.getFor(config.databaseMode);
    }

    synchronized void connect(DatabaseConfig config, File dataFolder) throws SQLException {
        logger.info("Connecting to %s database...", config.databaseMode);

        if (dataSource != null || connectionSource != null) {
            throw new IllegalStateException("DataSource is already connected");
        }

        HikariDataSource dataSource = dataSourceFactory.create(config);
        dataSourceConfigurer.configure(dataSource, config, dataFolder);

        if (dataSource.getJdbcUrl() == null) {
            throw new IllegalStateException("JDBC URL was not set by DataSourceConfigurer");
        }

        ConnectionSource connectionSource = new DataSourceConnectionSource(dataSource, dataSource.getJdbcUrl());

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

