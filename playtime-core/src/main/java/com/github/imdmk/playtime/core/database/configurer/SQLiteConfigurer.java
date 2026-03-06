package com.github.imdmk.playtime.core.database.configurer;

import com.github.imdmk.playtime.core.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class SQLiteConfigurer implements DataSourceConfigurer {

    private static final String JDBC_URL = "jdbc:sqlite:%s";

    private static final int MAX_POOL_SIZE = 2;
    private static final int MIN_IDLE = 1;

    @Override
    public void configure(HikariDataSource dataSource, DatabaseConfig config, File dataFolder) {
        Path dbPath = dataFolder.toPath().resolve(config.databaseFileName);

        try {
            Files.createDirectories(dbPath.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create SQLite directory: " + dbPath.getParent(), e);
        }

        String url = JDBC_URL.formatted(dbPath);

        dataSource.setJdbcUrl(url);
        dataSource.setMaximumPoolSize(MAX_POOL_SIZE);
        dataSource.setMinimumIdle(MIN_IDLE);
    }
}
