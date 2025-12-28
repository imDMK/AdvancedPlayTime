package com.github.imdmk.playtime.database;

import com.github.imdmk.playtime.database.driver.library.DriverLibraryLoader;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.validate.Validator;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public final class DatabaseManager {

    private final Plugin plugin;
    private final DatabaseConfig config;

    private final DriverLibraryLoader driverLoader;
    private final DatabaseConnector connector;

    public DatabaseManager(
            @NotNull Plugin plugin,
            @NotNull PluginLogger logger,
            @NotNull DatabaseConfig config
    ) {
        this.plugin = Validator.notNull(plugin, "plugin");
        this.config = Validator.notNull(config, "config");

        this.driverLoader = new DriverLibraryLoader(plugin);
        this.connector = new DatabaseConnector(logger, config);
    }

    public void loadDriver() {
        driverLoader.loadFor(config.databaseMode);
    }

    public void connect() throws SQLException {
        connector.connect(plugin.getDataFolder());
    }

    @Nullable
    public ConnectionSource getConnection() {
        return connector.getConnectionSource();
    }

    public void shutdown() {
        if (connector.isConnected()) {
            connector.close();
        }
    }
}
