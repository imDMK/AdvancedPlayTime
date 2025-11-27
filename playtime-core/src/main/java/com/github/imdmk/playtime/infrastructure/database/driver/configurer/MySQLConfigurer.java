package com.github.imdmk.playtime.infrastructure.database.driver.configurer;

import com.github.imdmk.playtime.infrastructure.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;

final class MySQLConfigurer implements DriverConfigurer {

    private static final String JDBC_URL = "jdbc:mysql://%s:%s/%s"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&useUnicode=true"
            + "&characterEncoding=utf8mb4"
            + "&serverTimezone=UTC"
            + "&connectionTimeZone=UTC"
            + "&tcpKeepAlive=true"
            + "&rewriteBatchedStatements=true"
            + "&socketTimeout=15000";

    @Override
    public void configure(@NotNull HikariDataSource dataSource, @NotNull DatabaseConfig config, @NotNull File dataFolder) {
        final String url = JDBC_URL.formatted(config.databaseHostName, config.port, config.databaseName);
        dataSource.setJdbcUrl(url);
    }
}
