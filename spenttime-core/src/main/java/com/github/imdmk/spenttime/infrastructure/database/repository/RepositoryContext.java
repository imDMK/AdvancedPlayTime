package com.github.imdmk.spenttime.infrastructure.database.repository;

import com.github.imdmk.spenttime.infrastructure.database.repository.ormlite.BaseDaoRepository;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;

/**
 * Context container providing shared infrastructure resources
 * for all repositories.
 *
 * <p>Currently encapsulates the {@link ExecutorService} responsible for
 * executing asynchronous database operations. This allows repositories
 * to offload blocking I/O work while maintaining a unified execution policy.</p>
 *
 * <p><strong>Usage:</strong> Injected into repository instances (see {@link BaseDaoRepository})
 * to provide consistent thread management for database access.</p>
 *
 * <p><strong>Threading:</strong> The supplied {@code dbExecutor} should be a dedicated,
 * bounded thread pool optimized for database I/O tasks — typically sized according
 * to connection pool limits or database concurrency capabilities.</p>
 *
 * @param dbExecutor the executor service used for running asynchronous database operations (non-null)
 *
 * @see BaseDaoRepository
 */
public record RepositoryContext(@NotNull ExecutorService dbExecutor) {
}
