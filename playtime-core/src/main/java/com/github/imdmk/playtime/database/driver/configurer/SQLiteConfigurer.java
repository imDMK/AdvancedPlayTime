package com.github.imdmk.playtime.database.driver.configurer;

import com.github.imdmk.playtime.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class SQLiteConfigurer implements DriverConfigurer {

    private static final String JDBC_URL = "jdbc:sqlite:%s";

    private static final int MAX_POOL_SIZE = 2;
    private static final int MIN_IDLE = 1;

    @Override
    public void configure(@NotNull HikariDataSource dataSource, @NotNull DatabaseConfig config, @NotNull File dataFolder) {
        final Path dbPath = dataFolder.toPath().resolve(config.databaseFileName);

        try {
            Files.createDirectories(dbPath.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create SQLite directory: " + dbPath.getParent(), e);
        }

        final String url = JDBC_URL.formatted(dbPath);

        dataSource.setJdbcUrl(url);
        dataSource.setMaximumPoolSize(MAX_POOL_SIZE);
        dataSource.setMinimumIdle(MIN_IDLE);
    }
}
