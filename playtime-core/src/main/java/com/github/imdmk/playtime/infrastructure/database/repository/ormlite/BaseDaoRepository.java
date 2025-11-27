package com.github.imdmk.playtime.infrastructure.database.repository.ormlite;

import com.github.imdmk.playtime.infrastructure.database.repository.Repository;
import com.github.imdmk.playtime.infrastructure.database.repository.RepositoryContext;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.Validator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Base class for ORMLite-backed repositories that manages DAO lifecycle,
 * schema bootstrapping, and asynchronous query execution.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Create required tables for the main entity and optional subclasses.</li>
 *   <li>Initialize and expose a typed {@link Dao} instance.</li>
 *   <li>Provide helper methods to run DAO work asynchronously with a bounded timeout.</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong> the {@link #dao} reference is {@code volatile} to ensure
 * visibility after initialization. Repository implementations should still avoid compound unsynchronized
 * operations on the DAO.</p>
 *
 * @param <T>  entity type handled by the repository
 * @param <ID> identifier type of the entity
 *
 * @see Dao
 * @see RepositoryContext
 * @see TableUtils
 */
public abstract class BaseDaoRepository<T, ID> implements Repository {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(6);

    private final PluginLogger logger;
    private final RepositoryContext context;

    protected volatile Dao<T, ID> dao;

    @Inject
    protected BaseDaoRepository(@NotNull PluginLogger logger, @NotNull RepositoryContext context) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.context = Validator.notNull(context, "context cannot be null");
        Logger.setGlobalLogLevel(Level.ERROR); // Change ORMLITE logging to errors
    }

    protected abstract Class<T> entityClass();
    protected abstract List<Class<?>> entitySubClasses();

    /**
     * Initializes the repository: creates missing tables and registers the DAO.
     *
     * <p>Tables for {@link #entityClass()} and all {@link #entitySubClasses()} are created if absent.
     * Then a new {@link Dao} is obtained via {@link DaoManager#createDao(ConnectionSource, Class)}.</p>
     *
     * @param source active ORMLite connection source
     * @throws SQLException if schema creation or DAO initialization fails
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
     * Closes the repository by unregistering the current DAO from its {@link ConnectionSource}.
     *
     * <p>This method is idempotent. If no DAO is set, it returns immediately.</p>
     */
    @Override
    public void close() {
        final Dao<T, ID> current = this.dao;
        if (current == null) {
            return;
        }

        this.dao = null;
        ConnectionSource source = current.getConnectionSource();
        if (source != null) {
            DaoManager.unregisterDao(source, current);
        }
    }

    /**
     * Executes the supplied task asynchronously on the repository executor with the default timeout.
     *
     * <p>Exceptions thrown by the supplier are logged and rethrown as {@link CompletionException}.
     * If the task exceeds timeout, the returned future completes exceptionally
     * with a {@link TimeoutException} wrapped in a {@link CompletionException}.</p>
     *
     * @param supplier unit of work to execute (non-null)
     * @param <R> result type
     * @return a future completed with the supplier result or exceptionally on failure/timeout
     * @throws NullPointerException if {@code supplier} is null
     */
    protected <R> CompletableFuture<R> executeAsync(@NotNull Supplier<R> supplier) {
        return executeAsync(supplier, DEFAULT_TIMEOUT);
    }

    /**
     * Executes the supplied task asynchronously on the repository executor with a custom timeout.
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>Runs on {@code context.dbExecutor()}.</li>
     *   <li>Logs and wraps exceptions into {@link CompletionException}.</li>
     *   <li>Applies {@link CompletableFuture#orTimeout(long, TimeUnit)} with the provided duration.</li>
     * </ul>
     *
     * @param supplier unit of work to execute (non-null)
     * @param timeout  maximum execution time (non-null)
     * @param <R> result type
     * @return a future completed with the supplier result or exceptionally on failure/timeout
     * @throws NullPointerException if {@code supplier} or {@code timeout} is null
     */
    protected <R> CompletableFuture<R> executeAsync(@NotNull Supplier<R> supplier, @NotNull Duration timeout) {
        Validator.notNull(supplier, "supplier cannot be null");
        Validator.notNull(timeout, "timeout cannot be null");

        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return supplier.get();
                    } catch (Exception e) {
                        logger.error(e, "Async DAO operation failed");
                        throw new CompletionException(e);
                    }
                }, this.context.dbExecutor())
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .exceptionally(e -> {
                    if (e instanceof TimeoutException) {
                        logger.warn("Async DAO operation timed out after %s ms", timeout.toMillis());
                    } else {
                        logger.error(e, "Async DAO operation failed (outer)");
                    }
                    throw (e instanceof CompletionException)
                            ? (CompletionException) e
                            : new CompletionException(e);
                });
    }

    /**
     * Executes the given runnable asynchronously on the repository executor with the default timeout.
     *
     * @param runnable task to run (non-null)
     * @return a future completed normally on success or exceptionally on failure/timeout
     * @throws NullPointerException if {@code runnable} is null
     */
    protected CompletableFuture<Void> executeAsync(@NotNull Runnable runnable) {
        return executeAsync(runnable, DEFAULT_TIMEOUT);
    }

    /**
     * Executes the given runnable asynchronously on the repository executor with a custom timeout.
     *
     * <p>Exceptions thrown by the runnable are logged and propagated as {@link CompletionException}.
     * On timeout, the future completes exceptionally with a {@link TimeoutException} wrapped in a
     * {@link CompletionException}.</p>
     *
     * @param runnable task to run (non-null)
     * @param timeout  maximum execution time (non-null)
     * @return a future completed normally on success or exceptionally on failure/timeout
     * @throws NullPointerException if {@code runnable} or {@code timeout} is null
     */
    protected CompletableFuture<Void> executeAsync(@NotNull Runnable runnable, @NotNull Duration timeout) {
        return CompletableFuture
                .runAsync(() -> {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        logger.error(e, "Async DAO operation failed");
                        throw new CompletionException(e);
                    }
                }, this.context.dbExecutor())
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .exceptionally(e -> {
                    if (e instanceof TimeoutException) {
                        logger.warn("Async DAO operation (void) timed out after %s ms", timeout.toMillis());
                    } else {
                        logger.error(e, "Async DAO operation failed (void outer)");
                    }
                    throw (e instanceof CompletionException)
                            ? (CompletionException) e
                            : new CompletionException(e);
                });
    }

    /**
     * Executes work requiring an initialized DAO, failing fast if the repository has not been started.
     *
     * <p>Use this to guard synchronous code paths that assume the DAO is ready.</p>
     *
     * @param work supplier executed with the current repository state (non-null)
     * @param <R> result type
     * @return the supplier's result
     * @throws IllegalStateException if the DAO is not initialized (e.g. {@link #start(ConnectionSource)} not called)
     * @throws NullPointerException if {@code work} is null
     */
    protected <R> R withDao(@NotNull Supplier<R> work) {
        Dao<T, ID> current = this.dao;
        if (current == null) {
            throw new IllegalStateException(getClass().getSimpleName() + ": DAO not initialized. Call RepositoryManager.startAll()");
        }

        return work.get();
    }
}
