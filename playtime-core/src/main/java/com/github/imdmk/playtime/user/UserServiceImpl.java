package com.github.imdmk.playtime.user;

import com.github.imdmk.playtime.UserDeleteEvent;
import com.github.imdmk.playtime.UserPreSaveEvent;
import com.github.imdmk.playtime.UserSaveEvent;
import com.github.imdmk.playtime.platform.events.BukkitEventCaller;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.validate.Validator;
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
        this.logger = Validator.notNull(logger, "logger");
        this.cache = Validator.notNull(cache, "cache");
        this.topUsersCache = Validator.notNull(topUsersCache, "topUsersCache");
        this.repository = Validator.notNull(repository, "repository");
        this.eventCaller = Validator.notNull(eventCaller, "eventCaller");
    }

    @Override
    public @NotNull Optional<User> findCachedByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid");
        return cache.getUserByUuid(uuid);
    }

    @Override
    public @NotNull Optional<User> findCachedByName(@NotNull String name) {
        Validator.notNull(name, "name");
        return cache.getUserByName(name);
    }

    @Override
    @Unmodifiable
    public @NotNull Collection<User> getCachedUsers() {
        return cache.getCache(); // returns unmodifiable
    }

    @Override
    public @NotNull CompletableFuture<Optional<User>> findByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid");

        Optional<User> cached = cache.getUserByUuid(uuid);
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
    public @NotNull CompletableFuture<Optional<User>> findByName(@NotNull String name) {
        Validator.notNull(name, "name");

        Optional<User> cached = cache.getUserByName(name);
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
    public @NotNull CompletableFuture<List<User>> findTopByPlayTime(int limit) {
        return topUsersCache.getTopByPlayTime(limit);
    }

    @Override
    public @NotNull CompletableFuture<User> save(@NotNull User user, @NotNull UserSaveReason reason) {
        Validator.notNull(user, "user");
        Validator.notNull(reason, "reason");

        eventCaller.callEvent(new UserPreSaveEvent(user, reason));

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
    public @NotNull CompletableFuture<UserDeleteResult> deleteByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid");
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
    public @NotNull CompletableFuture<UserDeleteResult> deleteByName(@NotNull String name) {
        Validator.notNull(name, "name");
        return repository.deleteByName(name)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(deleteResult -> {
                    eventCaller.callEvent(new UserDeleteEvent(deleteResult));
                    if (deleteResult.isSuccess()) {
                        cache.invalidateByName(name);
                    }
                    return deleteResult;
                })
                .exceptionally(e -> {
                    logger.error(e, "An error occurred while trying to delete user by name %s", name);
                    throw new RuntimeException(e);
                });
    }
}
