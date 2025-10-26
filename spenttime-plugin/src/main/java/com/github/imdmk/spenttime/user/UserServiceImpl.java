package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.UserDeleteEvent;
import com.github.imdmk.spenttime.UserSaveEvent;
import com.github.imdmk.spenttime.Validator;
import com.github.imdmk.spenttime.platform.events.BukkitEventCaller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.panda_lang.utilities.inject.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

final class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(2L);

    @Inject private UserCache cache;
    @Inject private UserRepository repository;
    @Inject private BukkitEventCaller bukkitEventCaller;

    @Override
    public @NotNull Optional<User> findCachedByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");
        return this.cache.getUserByUuid(uuid);
    }

    @Override
    public @NotNull Optional<User> findCachedByName(@NotNull String name) {
        Validator.notNull(name, "name cannot be null");
        return this.cache.getUserByName(name);
    }

    @Override
    @Unmodifiable
    public @NotNull Collection<User> getCachedUsers() {
        return this.cache.getCache(); // returns unmodifiable
    }

    @Override
    public @NotNull CompletableFuture<Optional<User>> findByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");

        Optional<User> cached = this.cache.getUserByUuid(uuid);
        if (cached.isPresent()) {
            return CompletableFuture.completedFuture(cached);
        }

        return this.repository.findByUuid(uuid)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(opt -> {
                    opt.ifPresent(this.cache::cacheUser);
                    return opt;
                })
                .whenComplete((u, e) -> {
                    if (e != null) {
                        LOGGER.error("An error occurred while trying to find user with id {}", uuid, e);
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<Optional<User>> findByName(@NotNull String name) {
        Validator.notNull(name, "name cannot be null");

        Optional<User> cached = this.cache.getUserByName(name);
        if (cached.isPresent()) {
            return CompletableFuture.completedFuture(cached);
        }

        return this.repository.findByName(name)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(opt -> {
                    opt.ifPresent(this.cache::cacheUser);
                    return opt;
                })
                .whenComplete((u, e) -> {
                    if (e != null) {
                        LOGGER.error("An error occurred while trying to find user with name {}", name, e);
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<List<User>> findTopBySpentTime(int limit) {
        if (limit <= 0) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return this.repository.findTopBySpentTime(limit)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .whenComplete(((users, e) -> {
                    if (e != null) {
                        LOGGER.error("An error occurred while trying to find top by spent time with limit {}", limit, e);
                    }
                }));
    }

    @Override
    public @NotNull CompletableFuture<User> save(@NotNull User user) {
        Validator.notNull(user, "user cannot be null");
        return this.repository.save(user)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(savedUser -> {
                    this.cache.cacheUser(savedUser);
                    this.bukkitEventCaller.callEvent(new UserSaveEvent(savedUser));
                    return savedUser;
                })
                .whenComplete((u, e) -> {
                    if (e != null) {
                        LOGGER.error("Failed to save user {}", user.getUuid(), e);
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<UserDeleteResult> deleteByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");
        return this.repository.deleteByUuid(uuid)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(deleteResult -> {
                    this.bukkitEventCaller.callEvent(new UserDeleteEvent(deleteResult));
                    if (deleteResult.isSuccess()) {
                        this.cache.invalidateByUuid(uuid);
                    }
                    return deleteResult;
                })
                .whenComplete((r, e) -> {
                    if (e != null) {
                        LOGGER.error("An error occurred while trying to delete user by uuid {}", uuid, e);
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<UserDeleteResult> deleteByName(@NotNull String name) {
        Validator.notNull(name, "name cannot be null");
        return this.repository.deleteByName(name)
                .orTimeout(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(deleteResult -> {
                    this.bukkitEventCaller.callEvent(new UserDeleteEvent(deleteResult));
                    if (deleteResult.isSuccess()) {
                        this.cache.invalidateByName(name);
                    }
                    return deleteResult;
                })
                .whenComplete((r, e) -> {
                    if (e != null) {
                        LOGGER.error("An error occurred while trying to delete user by name {}", name, e);
                    }
                });
    }
}
