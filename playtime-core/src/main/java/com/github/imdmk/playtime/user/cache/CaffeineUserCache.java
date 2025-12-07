package com.github.imdmk.playtime.user.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Caffeine-based implementation of {@link UserCache} with dual indexing by UUID and name.
 * Entries expire after a period of inactivity and after a maximum lifetime.
 */
public final class CaffeineUserCache implements UserCache {

    private static final Duration DEFAULT_EXPIRE_AFTER_ACCESS = Duration.ofHours(2);
    private static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofHours(12);

    private final Cache<UUID, User> cacheByUuid;
    private final Cache<String, UUID> cacheByName;

    public CaffeineUserCache(@NotNull Duration expireAfterAccess, @NotNull Duration expireAfterWrite) {
        Validator.notNull(expireAfterAccess, "expireAfterAccess");
        Validator.notNull(expireAfterWrite, "expireAfterWrite");

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

    public CaffeineUserCache() {
        this(DEFAULT_EXPIRE_AFTER_ACCESS, DEFAULT_EXPIRE_AFTER_WRITE);
    }

    @Override
    public void cacheUser(@NotNull User user) {
        Validator.notNull(user, "user");

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

    @Override
    public void invalidateUser(@NotNull User user) {
        Validator.notNull(user, "user");

        cacheByUuid.invalidate(user.getUuid());
        cacheByName.invalidate(user.getName());
    }

    @Override
    public void invalidateByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid");

        final User cached = cacheByUuid.getIfPresent(uuid);
        cacheByUuid.invalidate(uuid);
        if (cached != null) {
            cacheByName.invalidate(cached.getName());
        }
    }

    @Override
    public void invalidateByName(@NotNull String name) {
        Validator.notNull(name, "name");

        final UUID uuid = cacheByName.getIfPresent(name);
        if (uuid != null) {
            invalidateByUuid(uuid);
        } else {
            cacheByName.invalidate(name);
        }
    }

    @Override
    public @NotNull Optional<User> getUserByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid");
        return Optional.ofNullable(cacheByUuid.getIfPresent(uuid));
    }

    @Override
    public @NotNull Optional<User> getUserByName(@NotNull String name) {
        Validator.notNull(name, "name");

        final UUID uuid = cacheByName.getIfPresent(name);
        return uuid == null ? Optional.empty() : Optional.ofNullable(cacheByUuid.getIfPresent(uuid));
    }

    @Override
    public void updateUserNameMapping(@NotNull User user, @NotNull String oldName) {
        Validator.notNull(user, "user cannot be null");
        Validator.notNull(oldName, "oldName cannot be null");

        final String newName = user.getName();
        if (!oldName.equals(newName)) {
            cacheByName.invalidate(oldName);
            cacheByName.put(newName, user.getUuid());
        }

        cacheByUuid.put(user.getUuid(), user);
    }

    @Override
    public void forEachUser(@NotNull Consumer<User> action) {
        Validator.notNull(action, "action cannot be null");

        // Snapshot to avoid iterating over a live view while mutating the cache
        for (final User user : new ArrayList<>(cacheByUuid.asMap().values())) {
            action.accept(user);
        }
    }

    @Override
    @NotNull
    @Unmodifiable
    public Collection<User> getCache() {
        return List.copyOf(cacheByUuid.asMap().values());
    }

    @Override
    public void invalidateAll() {
        cacheByUuid.invalidateAll();
        cacheByName.invalidateAll();
    }
}
