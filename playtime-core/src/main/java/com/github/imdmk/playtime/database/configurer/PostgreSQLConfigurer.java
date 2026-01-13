package com.github.imdmk.playtime.database.configurer;

import com.github.imdmk.playtime.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;

final class PostgreSQLConfigurer implements DataSourceConfigurer {

    private static final String JDBC_URL = "jdbc:postgresql://%s:%s/%s"
            + "?sslmode=disable"
            + "&ApplicationName=PlayTime"
            + "&stringtype=unspecified";

    @Override
    public void configure(@NotNull HikariDataSource dataSource, @NotNull DatabaseConfig config, @NotNull File dataFolder) {
        final String url = JDBC_URL.formatted(config.databaseHostName, config.port, config.databaseName);
        dataSource.setJdbcUrl(url);
    }
}
