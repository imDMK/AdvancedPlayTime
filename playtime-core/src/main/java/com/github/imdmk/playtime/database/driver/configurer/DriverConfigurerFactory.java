package com.github.imdmk.playtime.database.driver.configurer;

import com.github.imdmk.playtime.database.DatabaseMode;
import com.github.imdmk.playtime.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class DriverConfigurerFactory {

    private static final Map<DatabaseMode, DriverConfigurer> CONFIGURER_BY_MODE = Map.of(
            DatabaseMode.MYSQL,      new MySQLConfigurer(),
            DatabaseMode.MARIADB,    new MariaDBConfigurer(),
            DatabaseMode.POSTGRESQL, new PostgreSQLConfigurer(),
            DatabaseMode.SQLITE,     new SQLiteConfigurer(),
            DatabaseMode.H2,         new H2Configurer(),
            DatabaseMode.SQL,        new SQLConfigurer()
    );

    public static @NotNull DriverConfigurer getFor(@NotNull DatabaseMode mode) {
        Validator.notNull(mode, "mode cannot be null");

        final DriverConfigurer configurer = CONFIGURER_BY_MODE.get(mode);
        if (configurer == null) {
            throw new IllegalArgumentException("Unsupported database mode: " + mode);
        }

        return configurer;
    }

    private DriverConfigurerFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
