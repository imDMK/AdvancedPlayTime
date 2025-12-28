package com.github.imdmk.playtime.database.driver.configurer;

import com.github.imdmk.playtime.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class H2Configurer implements DriverConfigurer {

    private static final String JDBC_URL = "jdbc:h2:file:%s"
            + ";MODE=MySQL"
            + ";DATABASE_TO_UPPER=false"
            + ";AUTO_SERVER=TRUE";

    @Override
    public void configure(@NotNull HikariDataSource dataSource, @NotNull DatabaseConfig config, @NotNull File dataFolder) {
        final Path dbPath = dataFolder.toPath().resolve(config.databaseFileName);

        try {
            Files.createDirectories(dbPath.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create H2 directory: " + dbPath.getParent(), e);
        }

        final String url = JDBC_URL.formatted(dbPath);
        dataSource.setJdbcUrl(url);
    }
}
