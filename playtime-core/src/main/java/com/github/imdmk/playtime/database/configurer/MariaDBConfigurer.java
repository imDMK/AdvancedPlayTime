package com.github.imdmk.playtime.database.configurer;

import com.github.imdmk.playtime.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;

final class MariaDBConfigurer implements DataSourceConfigurer {

    private static final String JDBC_URL = "jdbc:mariadb://%s:%s/%s"
            + "?useUnicode=true"
            + "&characterEncoding=utf8mb4"
            + "&serverTimezone=UTC"
            + "&tcpKeepAlive=true"
            + "&socketTimeout=15000";

    @Override
    public void configure(@NotNull HikariDataSource dataSource, @NotNull DatabaseConfig config, @NotNull File dataFolder) {
        final String url = JDBC_URL.formatted(config.databaseHostName, config.port, config.databaseName);
        dataSource.setJdbcUrl(url);
    }
}
