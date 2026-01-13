package com.github.imdmk.playtime.feature.playtime.top;

import com.github.imdmk.playtime.feature.playtime.PlayTimeUser;
import com.github.imdmk.playtime.feature.playtime.PlayTimeUserRepository;
import com.github.imdmk.playtime.injector.annotations.Service;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
final class PlayTimeTopService {

    private final PlayTimeTopCache cache;
    private final PlayTimeTopConfig config;
    private final PlayTimeUserRepository repository;

    @Inject
    PlayTimeTopService(
            @NotNull PlayTimeTopCache cache,
            @NotNull PlayTimeTopConfig config,
            @NotNull PlayTimeUserRepository repository
    ) {
        this.cache = cache;
        this.config = config;
        this.repository = repository;
    }

    CompletableFuture<List<PlayTimeUser>> getTop() {
        return getTopForDisplay(config.topUsersGuiLimit);
    }

    CompletableFuture<List<PlayTimeUser>> getTopForDisplay(int displayLimit) {
        if (displayLimit <= 0) {
            return CompletableFuture.completedFuture(List.of());
        }

        final Instant now = Instant.now();

        final Optional<CachedPlayTimeTop> cached = cache.get();
        if (cached.isPresent() && !cached.get().isExpired(config.topUsersCacheExpireAfter, now)) {
            return CompletableFuture.completedFuture(
                    slice(cached.get().users(), displayLimit)
            );
        }

        return repository.findTopByPlayTime(config.topUsersQueryLimit)
                .orTimeout(config.topUsersQueryTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(users -> {
                    cache.update(users);
                    return slice(users, displayLimit);
                });
    }

    void invalidateCache() {
        cache.invalidate();
    }

    private static List<PlayTimeUser> slice(List<PlayTimeUser> users, int limit) {
        return users.size() <= limit ? users : users.subList(0, limit);
    }
}


