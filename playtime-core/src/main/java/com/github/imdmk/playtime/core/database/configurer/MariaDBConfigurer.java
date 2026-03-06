package com.github.imdmk.playtime.core.database.configurer;

import com.github.imdmk.playtime.core.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

final class MariaDBConfigurer implements DataSourceConfigurer {

    private static final String JDBC_URL = "jdbc:mariadb://%s:%s/%s"
            + "?useUnicode=true"
            + "&characterEncoding=utf8mb4"
            + "&serverTimezone=UTC"
            + "&tcpKeepAlive=true"
            + "&socketTimeout=15000";

    @Override
    public void configure(HikariDataSource dataSource, DatabaseConfig config, File dataFolder) {
        String url = JDBC_URL.formatted(config.databaseHostName, config.port, config.databaseName);
        dataSource.setJdbcUrl(url);
    }
}
