package com.github.imdmk.playtime.core.database.configurer;

import com.github.imdmk.playtime.core.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class H2Configurer implements DataSourceConfigurer {

    private static final String JDBC_URL = "jdbc:h2:file:%s"
            + ";MODE=MySQL"
            + ";DATABASE_TO_UPPER=false"
            + ";AUTO_SERVER=TRUE";

    @Override
    public void configure(HikariDataSource dataSource, DatabaseConfig config, File dataFolder) {
        Path dbPath = dataFolder.toPath().resolve(config.databaseFileName);

        try {
            Files.createDirectories(dbPath.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create H2 directory: " + dbPath.getParent(), e);
        }

        String url = JDBC_URL.formatted(dbPath);
        dataSource.setJdbcUrl(url);
    }
}
