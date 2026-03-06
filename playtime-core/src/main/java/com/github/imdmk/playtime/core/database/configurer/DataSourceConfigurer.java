package com.github.imdmk.playtime.core.database.configurer;

import com.github.imdmk.playtime.core.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

@FunctionalInterface
public interface DataSourceConfigurer {

    void configure(HikariDataSource dataSource,
                   DatabaseConfig config,
                   File dataFolder);
}
