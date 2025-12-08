package com.github.imdmk.playtime.infrastructure.database.driver.configurer;

import com.github.imdmk.playtime.infrastructure.database.DatabaseMode;
import com.github.imdmk.playtime.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Factory responsible for selecting the correct {@link DriverConfigurer}
 * implementation for a given {@link DatabaseMode}.
 * <p>
 * All supported drivers are registered statically in an immutable lookup table.
 * This ensures fast resolution, avoids reflection, and cleanly separates
 * database-specific logic into dedicated strategy classes.
 * <p>
 * The factory acts as the single entry point for retrieving driver configuration
 * strategies used by {@code DatabaseConnector}.
 */
public final class DriverConfigurerFactory {

    /** Immutable lookup table mapping database modes to their respective configurers. */
    private static final Map<DatabaseMode, DriverConfigurer> CONFIGURER_BY_MODE = Map.of(
            DatabaseMode.MYSQL,      new MySQLConfigurer(),
            DatabaseMode.MARIADB,    new MariaDBConfigurer(),
            DatabaseMode.POSTGRESQL, new PostgreSQLConfigurer(),
            DatabaseMode.SQLITE,     new SQLiteConfigurer(),
            DatabaseMode.H2,         new H2Configurer(),
            DatabaseMode.SQL,        new SQLConfigurer()
    );

    /**
     * Returns the {@link DriverConfigurer} associated with the given {@link DatabaseMode}.
     *
     * @param mode the selected database engine (never null)
     * @return the matching non-null {@link DriverConfigurer}
     * @throws IllegalArgumentException if the mode is not supported
     */
    public static @NotNull DriverConfigurer getFor(@NotNull DatabaseMode mode) {
        Validator.notNull(mode, "mode cannot be null");

        DriverConfigurer configurer = CONFIGURER_BY_MODE.get(mode);
        if (configurer == null) {
            throw new IllegalArgumentException("Unsupported database mode: " + mode);
        }

        return configurer;
    }

    private DriverConfigurerFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}
