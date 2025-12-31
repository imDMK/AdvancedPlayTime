package com.github.imdmk.playtime.database.repository.ormlite;

import com.github.imdmk.playtime.database.repository.Repository;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class OrmLiteRepository<T, ID>
        implements Repository {

    private static final Duration EXECUTE_TIMEOUT = Duration.ofSeconds(3);

    protected final PluginLogger logger;
    protected final TaskScheduler scheduler;

    protected volatile Dao<T, ID> dao;

    protected OrmLiteRepository(@NotNull PluginLogger logger, @NotNull TaskScheduler scheduler) {
        this.logger = logger;
        this.scheduler = scheduler;
        Logger.setGlobalLogLevel(Level.ERROR); // Change ORMLITE logging to errors
    }

    protected abstract Class<T> entityClass();

    protected abstract List<Class<?>> entitySubClasses();

    @Override
    public void start(@NotNull ConnectionSource source) throws SQLException {
        for (final Class<?> subClass : this.entitySubClasses()) {
            TableUtils.createTableIfNotExists(source, subClass);
        }

        TableUtils.createTableIfNotExists(source, this.entityClass());
        this.dao = DaoManager.createDao(source, this.entityClass());
    }

    @Override
    public void close() {
        final Dao<T, ID> current = this.dao;
        if (current == null) {
            return;
        }

        this.dao = null;
        final ConnectionSource connection = current.getConnectionSource();
        if (connection != null) {
            DaoManager.unregisterDao(connection, current);
        }
    }

    protected <R> CompletableFuture<R> execute(@NotNull Supplier<R> supplier) {
        final CompletableFuture<R> future = new CompletableFuture<>();
        this.scheduler.runAsync(() -> {
            try {
                future.complete(supplier.get());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future.orTimeout(EXECUTE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
    }
}
