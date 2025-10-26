package com.github.imdmk.spenttime.user;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * High-level service for accessing and managing {@link User} data.
 * <p>
 * Provides both cache-only (synchronous, safe for main-thread use)
 * and asynchronous (database-backed) operations.
 * <p>
 * Implementations are expected to handle caching, persistence, and
 * consistency automatically.
 */
public interface UserService {

    /**
     * Finds a user by their unique UUID from the in-memory cache only.
     * <p>
     * This method is non-blocking and safe to call from the main server thread.
     *
     * @param uuid the user's UUID
     * @return an {@link Optional} containing the user if present in cache,
     *         or empty if not found
     */
    @NotNull Optional<User> findCachedByUuid(@NotNull UUID uuid);

    /**
     * Finds a user by their name from the in-memory cache only.
     * <p>
     * This method is non-blocking and safe to call from the main server thread.
     *
     * @param name the user's name (case-insensitive, depending on implementation)
     * @return an {@link Optional} containing the user if present in cache,
     *         or empty if not found
     */
    @NotNull Optional<User> findCachedByName(@NotNull String name);

    /**
     * Returns an unmodifiable snapshot of all users currently cached in memory.
     * <p>
     * This collection reflects a moment-in-time view and is not updated dynamically.
     * Safe to call from the main thread.
     *
     * @return a collection of cached {@link User} objects
     */
    @NotNull Collection<User> getCachedUsers();

    /**
     * Asynchronously finds a user by their UUID, using cache as the primary source
     * and the database as fallback.
     *
     * @param uuid the user's UUID
     * @return a {@link CompletableFuture} containing an {@link Optional} user
     *         when the lookup completes
     */
    @NotNull CompletableFuture<Optional<User>> findByUuid(@NotNull UUID uuid);

    /**
     * Asynchronously finds a user by their name, using cache as the primary source
     * and the database as fallback.
     *
     * @param name the user's name (case-insensitive, depending on implementation)
     * @return a {@link CompletableFuture} containing an {@link Optional} user
     *         when the lookup completes
     */
    @NotNull CompletableFuture<Optional<User>> findByName(@NotNull String name);

    /**
     * Asynchronously retrieves the top users sorted by total spent time, in descending order.
     *
     * @param limit the maximum number of users to return
     * @return a {@link CompletableFuture} containing a list of top users
     *         sorted by spent time descending
     */
    @NotNull CompletableFuture<List<User>> findTopBySpentTime(int limit);

    /**
     * Asynchronously saves a user to the underlying database and updates the cache.
     * <p>
     * If the user already exists, their data is updated.
     *
     * @param user the user to save
     * @return a {@link CompletableFuture} containing the saved user
     */
    @NotNull CompletableFuture<User> save(@NotNull User user);

    /**
     * Asynchronously deletes a user by their UUID from both the database and cache.
     *
     * @param uuid the UUID of the user to delete
     * @return a {@link CompletableFuture} completing with {@code true} if the user was deleted,
     *         or {@code false} if no such user existed
     */
    @NotNull CompletableFuture<UserDeleteResult> deleteByUuid(@NotNull UUID uuid);

    /**
     * Asynchronously deletes a user by their name from both the database and cache.
     *
     * @param name the name of the user to delete
     * @return a {@link CompletableFuture} completing with {@code true} if the user was deleted,
     *         or {@code false} if no such user existed
     */
    @NotNull CompletableFuture<UserDeleteResult> deleteByName(@NotNull String name);
}
