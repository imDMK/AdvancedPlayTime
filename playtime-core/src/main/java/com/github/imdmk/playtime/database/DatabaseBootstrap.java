package com.github.imdmk.playtime.database;

import com.github.imdmk.playtime.database.configurer.DataSourceConfigurer;
import com.github.imdmk.playtime.database.configurer.DataSourceConfigurerFactory;
import com.github.imdmk.playtime.database.library.DriverLibraryLoader;
import com.github.imdmk.playtime.injector.annotations.Database;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.io.File;
import java.sql.SQLException;

@Database
public final class DatabaseBootstrap {

    private final File dataFolder;
    private final DatabaseConfig config;

    private final DriverLibraryLoader libraryLoader;
    private final DataSourceConnector dataConnector;

    @Inject
    public DatabaseBootstrap(
            @NotNull Plugin plugin,
            @NotNull File dataFolder,
            @NotNull PluginLogger logger,
            @NotNull DatabaseConfig config
    ) {
        this.dataFolder = dataFolder;
        this.config = config;

        this.libraryLoader = new DriverLibraryLoader(plugin);

        final DataSourceConfigurer configurer = DataSourceConfigurerFactory.getFor(config.databaseMode);
        final DataSourceFactory factory = new DataSourceFactory();
        this.dataConnector = new DataSourceConnector(logger, factory, configurer);
    }

    public void start() {
        libraryLoader.loadFor(config.databaseMode);

        try {
            dataConnector.connect(config, dataFolder);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public ConnectionSource getConnection() {
        return dataConnector.getConnectionSource();
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    public void shutdown() {
        dataConnector.close();
    }
}
