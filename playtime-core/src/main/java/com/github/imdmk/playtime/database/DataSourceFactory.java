package com.github.imdmk.playtime.database;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

final class DataSourceFactory {

    static final String POOL_NAME = "playtime-db-pool";

    static final int MAX_POOL_SIZE = Math.max(4, Runtime.getRuntime().availableProcessors());
    static final int MIN_IDLE = 0;

    static final int CONNECTION_TIMEOUT = 10_000;
    static final int IDLE_TIMEOUT = 60_000;
    static final int MAX_LIFETIME = 600_000;

    static final Map<String, Object> SOURCE_PROPERTIES = Map.of(
            "cachePrepStmts", true,
            "prepStmtCacheSize", 250,
            "prepStmtCacheSqlLimit", 2048,
            "useServerPrepStmts", true
    );

    HikariDataSource create(@NotNull DatabaseConfig config) {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName(POOL_NAME);

        dataSource.setUsername(config.databaseUserName);
        dataSource.setPassword(config.databasePassword);

        dataSource.setMaximumPoolSize(MAX_POOL_SIZE);
        dataSource.setMinimumIdle(MIN_IDLE);

        dataSource.setConnectionTimeout(CONNECTION_TIMEOUT);
        dataSource.setIdleTimeout(IDLE_TIMEOUT);
        dataSource.setMaxLifetime(MAX_LIFETIME);

        SOURCE_PROPERTIES.forEach(dataSource::addDataSourceProperty);
        return dataSource;
    }
}

