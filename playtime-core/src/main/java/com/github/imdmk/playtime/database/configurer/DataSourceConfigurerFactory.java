package com.github.imdmk.playtime.database.configurer;

import com.github.imdmk.playtime.database.DatabaseMode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class DataSourceConfigurerFactory {

    private static final Map<DatabaseMode, DataSourceConfigurer> CONFIGURER_BY_MODE = Map.of(
            DatabaseMode.MYSQL,      new MySQLConfigurer(),
            DatabaseMode.MARIADB,    new MariaDBConfigurer(),
            DatabaseMode.POSTGRESQL, new PostgreSQLConfigurer(),
            DatabaseMode.SQLITE,     new SQLiteConfigurer(),
            DatabaseMode.H2,         new H2Configurer(),
            DatabaseMode.SQL,        new SQLConfigurer()
    );

    public static DataSourceConfigurer getFor(@NotNull DatabaseMode mode) {
        final DataSourceConfigurer configurer = CONFIGURER_BY_MODE.get(mode);
        if (configurer == null) {
            throw new IllegalArgumentException("Unsupported database mode: " + mode);
        }

        return configurer;
    }

    private DataSourceConfigurerFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
