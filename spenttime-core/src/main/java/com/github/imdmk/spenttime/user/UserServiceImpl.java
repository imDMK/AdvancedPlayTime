package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.UserDeleteEvent;
import com.github.imdmk.spenttime.UserPreSaveEvent;
import com.github.imdmk.spenttime.UserSaveEvent;
import com.github.imdmk.spenttime.platform.events.BukkitEventCaller;
import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

final class UserServiceImpl implements UserService {

    private static final Duration TIMEOUT = Duration.ofSeconds(2L);

    private final PluginLogger logger;
    private final UserCache cache;
    private final UserRepository repository;
    private final BukkitEventCaller eventCaller;

    @Inject
    UserServiceImpl(
            @NotNull PluginLogger logger,
            @NotNull UserCache cache,
            @NotNull UserRepository repository,
            @NotNull BukkitEventCaller eventCaller) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.cache = Validator.notNull(cache, "cache cannot be null");
        this.repository = Validator.notNull(repository, "repository cannot be null");
        this.eventCaller = Validator.notNull(eventCaller, "eventCaller cannot be null");
    }

    @Override
    public @NotNull Optional<User> findCachedByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");
        return cache.getUserByUuid(uuid);
    }

    @Override
    public @NotNull Optional<User> findCachedByName(@NotNull String name) {
        Validator.notNull(name, "name cannot be null");
        return cache.getUserByName(name);
    }

    @Override
    @Unmodifiable
    public @NotNull Collection<User> getCachedUsers() {
        return cache.getCache(); // returns unmodifiable
    }

    @Override
    public @NotNull CompletableFuture<Optional<User>> findByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");

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
        Validator.notNull(name, "name cannot be null");

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
    public @NotNull CompletableFuture<List<User>> findTopBySpentTime(int limit) {
        if (limit <= 0) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return repository.findTopBySpentTime(limit)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .exceptionally(e -> {
                    logger.error(e, "An error occurred while trying to find top by spent time with limit %s", limit);
                    throw new RuntimeException(e);
                });
    }

    @Override
    public @NotNull CompletableFuture<User> save(@NotNull User user, @NotNull UserSaveReason reason) {
        Validator.notNull(user, "user cannot be null");
        Validator.notNull(reason, "reason cannot be null");

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
        Validator.notNull(uuid, "uuid cannot be null");
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
        Validator.notNull(name, "name cannot be null");
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
