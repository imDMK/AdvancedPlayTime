package com.github.imdmk.playtime.core.database;

import com.github.imdmk.playtime.core.database.configurer.DataSourceConfigurer;
import com.github.imdmk.playtime.core.database.configurer.DataSourceConfigurerFactory;
import com.github.imdmk.playtime.core.injector.annotations.Database;
import com.github.imdmk.playtime.core.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.io.File;
import java.sql.SQLException;

@Database
public final class DatabaseBootstrap {

    private final File dataFolder;
    private final DatabaseConfig config;

    private final DataSourceConnector dataConnector;

    @Inject
    public DatabaseBootstrap(
            File dataFolder,
            PluginLogger logger,
            DatabaseConfig config
    ) {
        this.dataFolder = dataFolder;
        this.config = config;

        DataSourceConfigurer configurer = DataSourceConfigurerFactory.getFor(config.databaseMode);
        DataSourceFactory factory = new DataSourceFactory();
        this.dataConnector = new DataSourceConnector(logger, factory, configurer);
    }

    public void start() {
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
    private void shutdown() {
        dataConnector.close();
    }
}
