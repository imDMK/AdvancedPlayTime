package com.github.imdmk.playtime.user;

import com.github.imdmk.playtime.UserDeleteEvent;
import com.github.imdmk.playtime.UserSaveEvent;
import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.platform.event.BukkitEventCaller;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.user.cache.UserCache;
import com.github.imdmk.playtime.user.repository.UserRepository;
import com.github.imdmk.playtime.user.top.TopUsersCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service(priority = ComponentPriority.HIGH)
final class UserServiceImpl implements UserService {

    private static final Duration TIMEOUT = Duration.ofSeconds(2L);

    private final PluginLogger logger;
    private final UserCache cache;
    private final TopUsersCache topUsersCache;
    private final UserRepository repository;
    private final BukkitEventCaller eventCaller;

    @Inject
    UserServiceImpl(
            @NotNull PluginLogger logger,
            @NotNull UserCache cache,
            @NotNull TopUsersCache topUsersCache,
            @NotNull UserRepository repository,
            @NotNull BukkitEventCaller eventCaller
    ) {
        this.logger = logger;
        this.cache = cache;
        this.topUsersCache = topUsersCache;
        this.repository = repository;
        this.eventCaller = eventCaller;
    }

    @Override
    public Optional<User> findCachedByUuid(@NotNull UUID uuid) {
        return cache.getUserByUuid(uuid);
    }

    @Override
    public Optional<User> findCachedByName(@NotNull String name) {
        return cache.getUserByName(name);
    }

    @Override
    @Unmodifiable
    public Collection<User> getCachedUsers() {
        return cache.getCache(); // returns unmodifiable
    }

    @Override
    public CompletableFuture<Optional<User>> findByUuid(@NotNull UUID uuid) {
        final Optional<User> cached = cache.getUserByUuid(uuid);
        if (cached.isPresent()) {
            return CompletableFuture.completedFuture(cached);
        }

        return repository.findByUuid(uuid)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(opt -> {
                    opt.ifPresent(cache::cacheUser);
                    return opt;
                })
                .exceptionally(e -> {
                    logger.error(e, "An error occurred while trying to find user with id %s", uuid);
                    throw new RuntimeException(e);
                });
    }

    @Override
    public CompletableFuture<Optional<User>> findByName(@NotNull String name) {
        final Optional<User> cached = cache.getUserByName(name);
        if (cached.isPresent()) {
            return CompletableFuture.completedFuture(cached);
        }

        return repository.findByName(name)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(opt -> {
                    opt.ifPresent(cache::cacheUser);
                    return opt;
                })
                .exceptionally(e -> {
                    logger.error(e, "An error occurred while trying to find user with name %s", name);
                    throw new RuntimeException(e);
                });
    }

    @Override
    public CompletableFuture<List<User>> findTopByPlayTime(int limit) {
        return topUsersCache.getTopByPlayTime(limit);
    }

    @Override
    public CompletableFuture<User> save(@NotNull User user, @NotNull UserSaveReason reason) {
        return repository.save(user)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(saved -> {
                    eventCaller.callEvent(new UserSaveEvent(user, reason));
                    cache.cacheUser(saved);
                    return saved;
                })
                .exceptionally(e -> {
                    logger.error(e, "Failed to save user %s", user.getUuid());
                    throw new RuntimeException(e);
                });
    }

    @Override
    public CompletableFuture<UserDeleteResult> deleteByUuid(@NotNull UUID uuid) {
        return repository.deleteByUuid(uuid)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(result -> {
                    eventCaller.callEvent(new UserDeleteEvent(result));
                    if (result.isSuccess()) {
                        cache.invalidateByUuid(uuid);
                    }
                    return result;
                })
                .exceptionally(e -> {
                    logger.error(e, "An error occurred while trying to delete user by uuid %s", uuid);
                    throw new RuntimeException(e);
                });
    }

    @Override
    public CompletableFuture<Void> deleteByName(@NotNull String name) {
        return repository.deleteByName(name)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(deleteResult -> {
                    eventCaller.callEvent(new UserDeleteEvent(user));
                    cache.invalidateByName(name);
                    return deleteResult;
                })
                .exceptionally(e -> {
                    logger.error(e, "An error occurred while trying to delete user by name %s", name);
                    throw new RuntimeException(e);
                });
    }
}
