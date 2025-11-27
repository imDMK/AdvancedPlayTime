package com.github.imdmk.playtime.user.top;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class MemoryTopUsersCache implements TopUsersCache {

    private final PluginLogger logger;
    private final TopUsersCacheConfig config;
    private final UserRepository userRepository;

    private final AtomicReference<CachedLeaderboard> cachedLeaderboard = new AtomicReference<>();

    public MemoryTopUsersCache(
            @NotNull PluginLogger logger,
            @NotNull TopUsersCacheConfig config,
            @NotNull UserRepository userRepository
    ) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.config = Validator.notNull(config, "config cannot be null");
        this.userRepository = Validator.notNull(userRepository, "userRepository cannot be null");
    }

    @Override
    public CompletableFuture<List<User>> getTopByPlayTime() {
        return getTopByPlayTime(config.topUsersQueryLimit);
    }

    @Override
    public @NotNull CompletableFuture<List<User>> getTopByPlayTime(int limit) {
        if (limit <= 0) {
            return CompletableFuture.completedFuture(List.of());
        }

        final CachedLeaderboard cached = cachedLeaderboard.get();
        final Duration expireAfter = config.topUsersCacheExpireAfter;
        final Instant now = Instant.now();

        if (cached != null && cached.isUsable(limit, expireAfter, now)) {
            return CompletableFuture.completedFuture(slice(cached.users(), limit));
        }

        return loadTop(limit)
                .thenApply(users -> {
                    CachedLeaderboard updated = new CachedLeaderboard(users, limit, Instant.now());
                    cachedLeaderboard.set(updated);
                    return slice(updated.users(), limit);
                })
                .exceptionally(e -> {
                    logger.error(e, "Failed to load users top leaderboard (limit=%d)", limit);
                    return List.of();
                });
    }


    @Override
    public void invalidateAll() {
        cachedLeaderboard.set(null);
    }

    private CompletableFuture<List<User>> loadTop(int limit) {
        return userRepository.findTopByPlayTime(limit)
                .orTimeout(config.topUsersQueryTimeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    private static List<User> slice(List<User> users, int limit) {
        return users.size() <= limit ? users : users.subList(0, limit);
    }
}
