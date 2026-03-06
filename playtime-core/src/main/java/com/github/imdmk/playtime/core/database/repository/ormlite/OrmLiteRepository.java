package com.github.imdmk.playtime.core.database.repository.ormlite;

import com.github.imdmk.playtime.core.database.DatabaseBootstrap;
import com.github.imdmk.playtime.core.database.repository.RepositoryBootstrap;
import com.github.imdmk.playtime.core.database.repository.RepositoryInitializationException;
import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class OrmLiteRepository<T, ID>
        implements RepositoryBootstrap {

    private static final Duration EXECUTE_TIMEOUT = Duration.ofSeconds(3);

    protected final PluginLogger logger;
    protected final TaskScheduler scheduler;
    protected volatile Dao<T, ID> dao;

    private final DatabaseBootstrap databaseBootstrap;

    @Inject
    protected OrmLiteRepository(
            PluginLogger logger,
            TaskScheduler scheduler,
            DatabaseBootstrap databaseBootstrap
    ) {
        this.logger = logger;
        this.scheduler = scheduler;
        this.databaseBootstrap = databaseBootstrap;
        configureOrmLiteLogger();
    }

    protected abstract Class<T> entityClass();

    protected List<Class<?>> entitySubClasses() {
        return List.of();
    }

    @Override
    public void start() throws RepositoryInitializationException {
        ConnectionSource connection = databaseBootstrap.getConnection();
        if (connection == null) {
            throw new IllegalStateException("DatabaseBootstrap not started before repository initialization");
        }

        for (final Class<?> subClass : entitySubClasses()) {
            try {
                TableUtils.createTableIfNotExists(connection, subClass);
            } catch (SQLException e) {
                throw new RepositoryInitializationException(subClass, e);
            }
        }

        try {
            TableUtils.createTableIfNotExists(connection, entityClass());
            dao = DaoManager.createDao(connection, entityClass());
        } catch (SQLException e) {
            throw new RepositoryInitializationException(entityClass(), e);
        }
    }

    @Override
    public void close() {
        Dao<T, ID> current = dao;
        if (current == null) {
            return;
        }

        dao = null;
        ConnectionSource connection = current.getConnectionSource();
        if (connection != null) {
            DaoManager.unregisterDao(connection, current);
        }
    }

    protected <R> CompletableFuture<R> execute(Supplier<R> supplier) {
        if (dao == null) {
            throw new IllegalStateException("Repository not initialized or already closed");
        }

        CompletableFuture<R> future = new CompletableFuture<>();
        scheduler.runAsync(() -> {
            try {
                future.complete(supplier.get());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future.orTimeout(EXECUTE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
    }

    private static void configureOrmLiteLogger() {
        Logger.setGlobalLogLevel(Level.ERROR); // only errors
    }
}
