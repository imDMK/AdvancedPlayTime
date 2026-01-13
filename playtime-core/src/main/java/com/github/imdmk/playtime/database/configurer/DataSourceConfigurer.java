package com.github.imdmk.playtime.database.configurer;

import com.github.imdmk.playtime.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@FunctionalInterface
public interface DataSourceConfigurer {

    void configure(@NotNull HikariDataSource dataSource,
                   @NotNull DatabaseConfig config,
                   @NotNull File dataFolder);
}
