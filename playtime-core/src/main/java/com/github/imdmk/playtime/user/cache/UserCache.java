package com.github.imdmk.playtime.user.cache;

import com.github.imdmk.playtime.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Cache for {@link User} aggregates.
 * <p>
 * Implementations are expected to maintain a fast lookup by both UUID and name,
 * keep mappings consistent during updates, and guarantee thread-safety
 * if used in a concurrent environment.
 */
public interface UserCache {

    /**
     * Adds or replaces the given user in the cache.
     *
     * @param user the user instance to store
     */
    void cacheUser(@NotNull User user);

    /**
     * Removes the given user from the cache, if present.
     *
     * @param user the user instance to remove
     */
    void invalidateUser(@NotNull User user);

    /**
     * Removes a cached user by UUID.
     *
     * @param uuid the UUID to remove
     */
    void invalidateByUuid(@NotNull UUID uuid);

    /**
     * Removes a cached user by name (case-insensitive matching recommended).
     *
     * @param name the username to remove
     */
    void invalidateByName(@NotNull String name);

    /**
     * Retrieves a user by UUID.
     *
     * @param uuid the UUID to search
     * @return an {@link Optional} containing the cached user, if present
     */
    @NotNull Optional<User> getUserByUuid(@NotNull UUID uuid);

    /**
     * Retrieves a user by name.
     *
     * @param name the username to search
     * @return an {@link Optional} containing the cached user, if present
     */
    @NotNull Optional<User> getUserByName(@NotNull String name);

    /**
     * Updates internal name mappings for users whose username has changed.
     *
     * @param user    the user instance with the new name
     * @param oldName the previous username
     */
    void updateUserNameMapping(@NotNull User user, @NotNull String oldName);

    /**
     * Iterates over all cached users and executes the given action.
     *
     * @param action the callback executed for each cached user
     */
    void forEachUser(@NotNull Consumer<User> action);

    /**
     * Returns a view of all cached users.
     *
     * @return an unmodifiable collection of all cached users
     */
    @NotNull @Unmodifiable
    Collection<User> getCache();

    /**
     * Clears the entire cache.
     */
    void invalidateAll();
}
