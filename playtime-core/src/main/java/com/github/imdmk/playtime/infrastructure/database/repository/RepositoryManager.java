package com.github.imdmk.playtime.infrastructure.database.repository;

import com.github.imdmk.playtime.infrastructure.database.repository.ormlite.BaseDaoRepository;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.validate.Validator;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central coordinator that manages the lifecycle of all registered {@link Repository} instances.
 *
 * <p>This class provides thread-safe registration, startup, and shutdown of repositories.
 * It acts as the single entry point for initializing all repositories
 * once the plugin’s database connection has been established.</p>
 *
 * <p><strong>Thread-safety:</strong> Repository registration and iteration are backed by
 * {@link CopyOnWriteArrayList}, ensuring safe concurrent reads and registrations.</p>
 *
 * @see Repository
 * @see BaseDaoRepository
 * @see ConnectionSource
 */
public final class RepositoryManager implements AutoCloseable {

    private final PluginLogger logger;

    private final List<Repository> repositories = new CopyOnWriteArrayList<>();

    public RepositoryManager(@NotNull PluginLogger logger) {
        this.logger = Validator.notNull(logger, "logger must not be null");
    }

    /**
     * Registers a single repository instance for lifecycle management.
     *
     * <p>If the repository has already been registered, the operation is skipped
     * and a warning is logged.</p>
     *
     * @param repository non-null repository instance
     * @throws NullPointerException if {@code repository} is null
     */
    public void register(@NotNull Repository repository) {
        Validator.notNull(repository, "repository cannot be null");
        if (repositories.contains(repository)) {
            logger.warn("Repository %s already registered — skipping", repository.getClass().getSimpleName());
            return;
        }

        repositories.add(repository);
    }

    /**
     * Registers multiple repository instances at once.
     *
     * @param repositories one or more non-null repositories
     * @throws NullPointerException if {@code repositories} array or any element is null
     */
    public void register(@NotNull Repository... repositories) {
        Validator.notNull(repositories, "repositories cannot be null");
        for (final Repository repository : repositories) {
            this.register(repository);
        }
    }

    /**
     * Starts all registered repositories using the provided {@link ConnectionSource}.
     *
     * <p>This method creates required tables and initializes all DAO layers.
     * If any repository fails to start, the exception is logged and rethrown,
     * stopping further startup to prevent inconsistent state.</p>
     *
     * @param connectionSource non-null active database connection source
     * @throws SQLException if a repository fails to start
     * @throws NullPointerException if {@code connectionSource} is null
     */
    public void startAll(@NotNull ConnectionSource connectionSource) throws SQLException {
        Validator.notNull(connectionSource, "connectionSource cannot be null");
        for (final Repository repository : repositories) {
            try {
                repository.start(connectionSource);
            } catch (SQLException e) {
                logger.error(e, "Failed to start repository: %s", repository.getClass().getSimpleName());
                throw e;
            }
        }
    }

    /**
     * Gracefully closes all registered repositories.
     *
     * <p>Each repository’s {@link Repository#close()} method is invoked individually.
     * Exceptions during closing are caught and logged as warnings, allowing
     * all repositories to attempt shutdown even if one fails.</p>
     */
    @Override
    public void close() {
        for (final Repository repository : repositories) {
            try {
                repository.close();
            } catch (Exception e) {
                logger.warn(e, "Error while closing repository: %s", repository.getClass().getSimpleName());
            }
        }
    }
}
