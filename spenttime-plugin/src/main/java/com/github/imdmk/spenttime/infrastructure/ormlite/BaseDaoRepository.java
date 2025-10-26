package com.github.imdmk.spenttime.infrastructure.ormlite;

import com.github.imdmk.spenttime.Validator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Base class for all ORMLite-based repositories.
 * <p>
 * Provides DAO initialization, asynchronous query execution, and
 * automatic table creation for entity classes.
 *
 * @param <T>  entity type
 * @param <ID> entity identifier type
 */
public abstract class BaseDaoRepository<T, ID> implements Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDaoRepository.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(6);

    @Inject private RepositoryContext context;

    protected volatile Dao<T, ID> dao;

    /** @return the primary entity class managed by this repository */
    protected abstract Class<T> entityClass();

    /** @return additional entity subclasses to initialize (optional) */
    protected abstract List<Class<?>> entitySubClasses();

    /**
     * Initializes the repository and creates all required tables if they do not exist.
     */
    @Override
    public void start(@NotNull ConnectionSource source) throws SQLException {
        for (Class<?> subClass : this.entitySubClasses()) {
            TableUtils.createTableIfNotExists(source, subClass);
        }

        TableUtils.createTableIfNotExists(source, this.entityClass());
        this.dao = DaoManager.createDao(source, this.entityClass());
    }

    /**
     * Closes and unregisters the current DAO from the connection source.
     */
    @Override
    public void close() {
        final Dao<T, ID> current = this.dao;
        if (current == null) {
            return;
        }

        this.dao = null;
        ConnectionSource connectionSource = current.getConnectionSource();
        if (connectionSource != null) {
            DaoManager.unregisterDao(connectionSource, current);
        }
    }

    /**
     * Executes a supplier asynchronously using the repository executor.
     */
    protected <R> CompletableFuture<R> executeAsync(@NotNull Supplier<R> supplier) {
        return executeAsync(supplier, DEFAULT_TIMEOUT);
    }

    /**
     * Executes a supplier asynchronously with a custom timeout.
     */
    protected <R> CompletableFuture<R> executeAsync(@NotNull Supplier<R> supplier, @NotNull Duration timeout) {
        Validator.notNull(supplier, "supplier cannot be null");
        Validator.notNull(timeout, "timeout cannot be null");

        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return supplier.get();
                    } catch (Exception e) {
                        LOGGER.error("Async DAO operation failed", e);
                        throw new CompletionException(e);
                    }
                }, this.context.executor())
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .exceptionally(e -> {
                    if (e instanceof TimeoutException) {
                        LOGGER.warn("Async DAO operation timed out after {} ms", timeout.toMillis());
                    } else {
                        LOGGER.error("Async DAO operation failed (outer)", e);
                    }
                    throw (e instanceof CompletionException)
                            ? (CompletionException) e
                            : new CompletionException(e);
                });
    }

    /**
     * Executes a runnable asynchronously using the repository executor.
     */
    protected CompletableFuture<Void> executeAsync(@NotNull Runnable runnable) {
        return executeAsync(runnable, DEFAULT_TIMEOUT);
    }

    /**
     * Executes a runnable asynchronously with a custom timeout.
     */
    protected CompletableFuture<Void> executeAsync(@NotNull Runnable runnable, @NotNull Duration timeout) {
        return CompletableFuture
                .runAsync(() -> {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        LOGGER.error("Async DAO operation failed", e);
                        throw new CompletionException(e);
                    }
                }, this.context.executor())
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .exceptionally(e -> {
                    if (e instanceof TimeoutException) {
                        LOGGER.warn("Async DAO operation (void) timed out after {} ms", timeout.toMillis());
                    } else {
                        LOGGER.error("Async DAO operation failed (void outer)", e);
                    }
                    throw (e instanceof CompletionException)
                            ? (CompletionException) e
                            : new CompletionException(e);
                });
    }

    /**
     * Ensures DAO is initialized and executes work within its context.
     *
     * @throws IllegalStateException if DAO is not initialized
     */
    protected <R> R withDao(@NotNull Supplier<R> work) {
        Dao<T, ID> current = this.dao;
        if (current == null) {
            throw new IllegalStateException(getClass().getSimpleName() + ": DAO not initialized. Call RepositoryManager.startAll()");
        }

        return work.get();
    }
}
