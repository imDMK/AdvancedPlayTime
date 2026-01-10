package com.github.imdmk.playtime.user.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service(priority = ComponentPriority.LOWEST)
public final class CaffeineUserCache implements UserCache {

    private static final Duration EXPIRE_AFTER_ACCESS = Duration.ofHours(2);
    private static final Duration EXPIRE_AFTER_WRITE = Duration.ofHours(12);

    private final Cache<UUID, User> cacheByUuid;
    private final Cache<String, UUID> cacheByName;

    @Inject
    public CaffeineUserCache() {
        this.cacheByName = Caffeine.newBuilder()
                .expireAfterWrite(EXPIRE_AFTER_WRITE)
                .expireAfterAccess(EXPIRE_AFTER_ACCESS)
                .build();

        this.cacheByUuid = Caffeine.newBuilder()
                .expireAfterWrite(EXPIRE_AFTER_WRITE)
                .expireAfterAccess(EXPIRE_AFTER_ACCESS)
                .removalListener((UUID key, User user, RemovalCause cause) -> {
                    if (key != null && user != null) {
                        this.cacheByName.invalidate(user.getName());
                    }
                })
                .build();
    }

    @Override
    public void cacheUser(@NotNull User user) {
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
        cacheByUuid.invalidate(user.getUuid());
        cacheByName.invalidate(user.getName());
    }

    @Override
    public void invalidateByUuid(@NotNull UUID uuid) {
        final User cached = cacheByUuid.getIfPresent(uuid);
        cacheByUuid.invalidate(uuid);
        if (cached != null) {
            cacheByName.invalidate(cached.getName());
        }
    }

    @Override
    public void invalidateByName(@NotNull String name) {
        final UUID uuid = cacheByName.getIfPresent(name);
        if (uuid != null) {
            invalidateByUuid(uuid);
        } else {
            cacheByName.invalidate(name);
        }
    }

    @Override
    @Subscribe(event = PlayTimeShutdownEvent.class)
    public void invalidateAll() {
        cacheByUuid.invalidateAll();
        cacheByName.invalidateAll();
    }

    @Override
    public Optional<User> getUserByUuid(@NotNull UUID uuid) {
        return Optional.ofNullable(cacheByUuid.getIfPresent(uuid));
    }

    @Override
    public Optional<User> getUserByName(@NotNull String name) {
        final UUID uuid = cacheByName.getIfPresent(name);
        return uuid == null ? Optional.empty() : Optional.ofNullable(cacheByUuid.getIfPresent(uuid));
    }

    @Override
    public void updateUserNameMapping(@NotNull User user, @NotNull String oldName) {
        final String newName = user.getName();
        if (!oldName.equals(newName)) {
            cacheByName.invalidate(oldName);
            cacheByName.put(newName, user.getUuid());
        }

        cacheByUuid.put(user.getUuid(), user);
    }

    @Override
    public void forEachUser(@NotNull Consumer<User> action) {
        // Snapshot to avoid iterating over a live view while mutating the cache
        for (final User user : new ArrayList<>(cacheByUuid.asMap().values())) {
            action.accept(user);
        }
    }

    @Override
    @Unmodifiable
    public Collection<User> getCache() {
        return List.copyOf(cacheByUuid.asMap().values());
    }
}
