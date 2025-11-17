package com.github.imdmk.spenttime.user.repository;

import com.github.imdmk.spenttime.infrastructure.database.repository.Repository;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserDeleteResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous repository for managing and querying {@link User} records.
 * <p>All methods return non-null {@link CompletableFuture}s and complete exceptionally on failure.</p>
 * <p>Name matching policy should be documented by the implementation (recommended: case-insensitive, normalized).</p>
 */
public interface UserRepository extends Repository {

    /**
     * Finds a user by UUID.
     *
     * @param uuid non-null UUID
     * @return non-null future with an Optional user (empty if not found)
     */
    @NotNull CompletableFuture<Optional<User>> findByUuid(@NotNull UUID uuid);

    /**
     * Finds a user by exact name (implementation should document case handling).
     *
     * @param name non-null username
     * @return non-null future with an Optional user (empty if not found)
     */
    @NotNull CompletableFuture<Optional<User>> findByName(@NotNull String name);

    /**
     * Retrieves all users from the data source.
     * <p>
     * The returned list order is implementation-defined unless otherwise documented.
     * Implementations are expected to return an immutable, non-null list, and may apply
     * internal caching or batching strategies for performance.
     * </p>
     *
     * @return non-null future with all persisted users (possibly empty)
     */
    @NotNull CompletableFuture<List<User>> findAll();

    /**
     * Returns top users by spent time, sorted descending.
     * Ties are resolved deterministically (e.g., by UUID ascending).
     *
     * @param limit number of users to return; must be &gt; 0
     * @return non-null future with an immutable list (possibly empty)
     */
    @NotNull CompletableFuture<List<User>> findTopBySpentTime(long limit);

    /**
     * Creates or updates the user (upsert).
     *
     * @param user non-null user
     * @return non-null future with the persisted user
     */
    @NotNull CompletableFuture<User> save(@NotNull User user);

    /**
     * Deletes a user by UUID.
     *
     * @param uuid non-null UUID
     * @return non-null future that completes with {@code true} if a row was deleted, otherwise {@code false}
     */
    @NotNull CompletableFuture<UserDeleteResult> deleteByUuid(@NotNull UUID uuid);

    /**
     * Deletes a user by name.
     *
     * @param name non-null username
     * @return non-null future that completes with {@code true} if a row was deleted, otherwise {@code false}
     */
    @NotNull CompletableFuture<UserDeleteResult> deleteByName(@NotNull String name);
}
