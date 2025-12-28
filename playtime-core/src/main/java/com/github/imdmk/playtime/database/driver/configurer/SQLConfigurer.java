package com.github.imdmk.playtime.database.driver.configurer;

import com.github.imdmk.playtime.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;

final class SQLConfigurer implements DriverConfigurer {

    private static final String JDBC_URL = "jdbc:sqlserver://%s:%s"
            + ";databaseName=%s"
            + ";encrypt=false"
            + ";trustServerCertificate=true"
            + ";loginTimeout=15";

    @Override
    public void configure(@NotNull HikariDataSource dataSource, @NotNull DatabaseConfig config, @NotNull File dataFolder) {
        final String url = JDBC_URL.formatted(config.databaseHostName, config.port, config.databaseName);
        dataSource.setJdbcUrl(url);
    }
}
