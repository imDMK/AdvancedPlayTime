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
public final class DatabaseManagerImpl implements DatabaseManager {

    private static final DataSourceFactory FACTORY = new DataSourceFactory();

    private final File dataFolder;
    private final DatabaseConfig config;

    private final DataSourceConnector dataConnector;

    @Inject
    public DatabaseManagerImpl(
            File dataFolder,
            PluginLogger logger,
            DatabaseConfig config
    ) {
        this.dataFolder = dataFolder;
        this.config = config;
        this.dataConnector = new DataSourceConnector(logger, FACTORY, config);
    }

    @Override
    public void start() throws SQLException {
        dataConnector.connect(config, dataFolder);
    }

    @Override
    @Nullable
    public ConnectionSource getConnectionSource() {
        return dataConnector.getConnectionSource();
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        dataConnector.close();
    }
}
