package com.github.imdmk.spenttime.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.imdmk.spenttime.shared.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Thread-safe in-memory cache for {@link User} instances.
 * <p>
 * Uses two Caffeine caches:
 * <ul>
 *     <li>UUID → User (primary store)</li>
 *     <li>Name → UUID (secondary index)</li>
 * </ul>
 * Expiration and eviction policies are configurable. Evictions automatically
 * remove corresponding entries from the name index to keep both caches consistent.
 */
final class UserCache {

    private static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofHours(12);
    private static final Duration DEFAULT_EXPIRE_AFTER_ACCESS = Duration.ofHours(2);

    private final Cache<UUID, User> cacheByUuid;
    private final Cache<String, UUID> cacheByName;

    /**
     * Creates a new {@code UserCache} with custom expiration and size settings.
     *
     * @param expireAfterAccess duration after which entries expire if not accessed
     * @param expireAfterWrite  duration after which entries expire after write
     */
    public UserCache(@NotNull Duration expireAfterAccess,
                     @NotNull Duration expireAfterWrite) {

        this.cacheByName = Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWrite)
                .expireAfterAccess(expireAfterAccess)
                .build();

        this.cacheByUuid = Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWrite)
                .expireAfterAccess(expireAfterAccess)
                .removalListener((UUID key, User user, RemovalCause cause) -> {
                    if (key != null && user != null) {
                        this.cacheByName.invalidate(user.getName());
                    }
                })
                .build();
    }

    /**
     * Creates a new {@code UserCache} with default expiration and size limits.
     */
    public UserCache() {
        this(DEFAULT_EXPIRE_AFTER_ACCESS, DEFAULT_EXPIRE_AFTER_WRITE);
    }

    /**
     * Adds or updates a user in the cache and synchronizes both UUID and name indexes.
     *
     * @param user non-null user instance
     * @throws NullPointerException if {@code user} is null
     */
    public void cacheUser(@NotNull User user) {
        Validator.notNull(user, "user cannot be null");

        final UUID uuid = user.getUuid();
        final String name = user.getName();

        final User previous = cacheByUuid.getIfPresent(uuid);
        if (previous != null) {
            final String oldName = previous.getName();
            if (!oldName.equals(name)) {
                cacheByName.invalidate(oldName);
            }
        }

        cacheByUuid.put(uuid, user);
        cacheByName.put(name, uuid);
    }

    /**
     * Removes a user from both caches.
     *
     * @param user non-null user instance
     */
    public void invalidateUser(@NotNull User user) {
        Validator.notNull(user, "user cannot be null");

        cacheByUuid.invalidate(user.getUuid());
        cacheByName.invalidate(user.getName());
    }

    /**
     * Removes a user by UUID.
     *
     * @param uuid non-null user UUID
     */
    public void invalidateByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");

        final User cached = cacheByUuid.getIfPresent(uuid);
        cacheByUuid.invalidate(uuid);
        if (cached != null) {
            cacheByName.invalidate(cached.getName());
        }
    }

    /**
     * Removes a user by name.
     *
     * @param name non-null username
     */
    public void invalidateByName(@NotNull String name) {
        Validator.notNull(name, "name cannot be null");

        final UUID uuid = cacheByName.getIfPresent(name);
        if (uuid != null) {
            invalidateByUuid(uuid);
        } else {
            cacheByName.invalidate(name);
        }
    }

    /**
     * Retrieves a user by UUID.
     *
     * @param uuid non-null user UUID
     * @return optional user if present
     */
    public @NotNull Optional<User> getUserByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");
        return Optional.ofNullable(cacheByUuid.getIfPresent(uuid));
    }

    /**
     * Retrieves a user by name (case-insensitive).
     *
     * @param name non-null username
     * @return optional user if present
     */
    public @NotNull Optional<User> getUserByName(@NotNull String name) {
        Validator.notNull(name, "name cannot be null");
        final UUID uuid = cacheByName.getIfPresent(name);
        return uuid == null ? Optional.empty() : Optional.ofNullable(cacheByUuid.getIfPresent(uuid));
    }

    /**
     * Updates the username mapping after a player rename.
     *
     * @param user    non-null updated user
     * @param oldName non-null previous name
     */
    public void updateUserNameMapping(@NotNull User user, @NotNull String oldName) {
        Validator.notNull(user, "user cannot be null");
        Validator.notNull(oldName, "oldName  cannot be null");

        final String newName = user.getName();
        if (!oldName.equals(newName)) {
            cacheByName.invalidate(oldName);
            cacheByName.put(newName, user.getUuid());
        }

        cacheByUuid.put(user.getUuid(), user);
    }

    /**
     * Applies a given action to all cached users.
     *
     * @param action non-null consumer to apply
     */
    public void forEachUser(@NotNull Consumer<User> action) {
        Validator.notNull(action, "action cannot be null");

        for (final User user : cacheByUuid.asMap().values()) {
            final String oldName = user.getName();

            action.accept(user);
            updateUserNameMapping(user, oldName);
        }
    }

    /**
     * Returns an unmodifiable snapshot of all cached usernames.
     *
     * @return collection of cached usernames
     */
    @NotNull
    @Unmodifiable
    public Collection<User> getCache() {
        return Collections.unmodifiableCollection(new ArrayList<>(cacheByUuid.asMap().values()));
    }

    /**
     * Clears both caches completely.
     */
    public void clearCache() {
        cacheByUuid.invalidateAll();
        cacheByName.invalidateAll();
    }
}
