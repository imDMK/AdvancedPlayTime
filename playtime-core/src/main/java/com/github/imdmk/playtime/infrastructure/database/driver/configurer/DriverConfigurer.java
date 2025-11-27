package com.github.imdmk.playtime.infrastructure.database.driver.configurer;

import com.github.imdmk.playtime.infrastructure.database.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Strategy interface defining how to configure a {@link HikariDataSource}
 * for a specific database engine.
 * <p>
 * Implementations are responsible for:
 * <ul>
 *     <li>constructing the correct JDBC URL,</li>
 *     <li>applying engine-specific HikariCP properties,</li>
 *     <li>performing any required filesystem preparation (e.g. SQLite/H2 directories).</li>
 * </ul>
 * This abstraction allows {@link com.github.imdmk.playtime.infrastructure.database.DatabaseConnector}
 * to remain engine-agnostic while still supporting multiple database types.
 */
public interface DriverConfigurer {

    /**
     * Configures the provided {@link HikariDataSource} instance using the database
     * settings supplied in {@link DatabaseConfig} and the plugin data folder.
     * <p>
     * Implementations must be deterministic and side-effect-free except for:
     * <ul>
     *     <li>modifying the {@code dataSource} instance,</li>
     *     <li>creating required directories for file-based databases.</li>
     * </ul>
     *
     * @param dataSource the HikariCP data source to configure (never null)
     * @param config     the database configuration containing connection details (never null)
     * @param dataFolder the plugin data folder, used especially for file-based engines like SQLite/H2 (never null)
     */
    void configure(@NotNull HikariDataSource dataSource,
                   @NotNull DatabaseConfig config,
                   @NotNull File dataFolder);
}
