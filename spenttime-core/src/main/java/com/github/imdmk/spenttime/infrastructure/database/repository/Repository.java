package com.github.imdmk.spenttime.infrastructure.database.repository;

import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * Base contract for all repositories.
 * <p>
 * Provides lifecycle hooks for database initialization and cleanup.
 * Implementations should create their DAO bindings in {@link #start(ConnectionSource)}
 * and release resources in {@link #close()}.
 */
public interface Repository extends AutoCloseable {

    /**
     * Initializes repository to the given connection source.
     *
     * @param source the ORMLite connection source
     * @throws SQLException if database initialization fails
     */
    void start(@NotNull ConnectionSource source) throws SQLException;

    /** Closes the repository and releases all resources. */
    @Override
    void close();
}
