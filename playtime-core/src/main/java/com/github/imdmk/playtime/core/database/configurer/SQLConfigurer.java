package com.github.imdmk.playtime.core.database.configurer;

import com.github.imdmk.playtime.core.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

final class SQLConfigurer implements DataSourceConfigurer {

    private static final String JDBC_URL = "jdbc:sqlserver://%s:%s"
            + ";databaseName=%s"
            + ";encrypt=false"
            + ";trustServerCertificate=true"
            + ";loginTimeout=15";

    @Override
    public void configure(HikariDataSource dataSource, DatabaseConfig config, File dataFolder) {
        String url = JDBC_URL.formatted(config.databaseHostName, config.port, config.databaseName);
        dataSource.setJdbcUrl(url);
    }
}
