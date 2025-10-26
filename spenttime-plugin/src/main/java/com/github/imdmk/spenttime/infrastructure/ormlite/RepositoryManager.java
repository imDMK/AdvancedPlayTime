package com.github.imdmk.spenttime.infrastructure.ormlite;

import com.github.imdmk.spenttime.Validator;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central manager responsible for registering and controlling the lifecycle
 * of all repositories used by the bot. Provides thread-safe registration,
 * startup, and graceful shutdown.
 */
public final class RepositoryManager implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryManager.class);

    private final List<Repository> repositories = new CopyOnWriteArrayList<>();

    /** Register a single repository. */
    public void register(@NotNull Repository repository) {
        Validator.notNull(repository, "repository cannot be null");
        if (this.repositories.contains(repository)) {
            LOGGER.warn("Repository {} already registered — skipping", repository.getClass().getSimpleName());
            return;
        }

        this.repositories.add(repository);
        LOGGER.debug("Registered repository: {}", repository.getClass().getSimpleName());
    }

    /** Register multiple repositories. */
    public void register(@NotNull Repository... repositories) {
        Validator.notNull(repositories, "repositories cannot be null");
        for (final Repository repository : repositories) {
            this.register(repository);
        }
    }

    /** Start all registered repositories. */
    public void startAll(@NotNull ConnectionSource connectionSource) throws SQLException {
        Validator.notNull(connectionSource, "connectionSource cannot be null");
        for (final Repository repository : this.repositories) {
            try {
                repository.start(connectionSource);
                LOGGER.debug("Started repository: {}", repository.getClass().getSimpleName());
            } catch (SQLException e) {
                LOGGER.error("Failed to start repository: {}", repository.getClass().getSimpleName(), e);
                throw e;
            }
        }
    }

    /** Gracefully close all registered repositories. */
    @Override
    public void close() {
        for (final Repository repository : this.repositories) {
            try {
                repository.close();
                LOGGER.debug("Closed repository: {}", repository.getClass().getSimpleName());
            } catch (Exception e) {
                LOGGER.warn("Error while closing repository: {}", repository.getClass().getSimpleName(), e);
            }
        }
    }
}
