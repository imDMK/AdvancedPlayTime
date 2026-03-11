package com.github.imdmk.playtime.core.feature.playtime.top;

import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;
import com.github.imdmk.playtime.core.feature.playtime.repository.PlayTimeUserRepository;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public final class PlayTimeTopService {

    private final PlayTimeTopCache cache;
    private final PlayTimeTopConfig config;
    private final PlayTimeUserRepository repository;

    @Inject
    PlayTimeTopService(
            PlayTimeTopCache cache,
            PlayTimeTopConfig config,
            PlayTimeUserRepository repository
    ) {
        this.cache = cache;
        this.config = config;
        this.repository = repository;
    }

    public CompletableFuture<List<PlayTimeUser>> getTop() {
        return getTopForDisplay(config.topUsersGuiLimit);
    }

    public CompletableFuture<List<PlayTimeUser>> getTopForDisplay(int displayLimit) {
        if (displayLimit <= 0) {
            return CompletableFuture.completedFuture(List.of());
        }

        Instant now = Instant.now();

        PlayTimeTop cached = cache.get();
        if (cached != null && !cached.isExpired(config.topUsersCacheExpireAfter, now)) {
            return CompletableFuture.completedFuture(
                    slice(cached.users(), displayLimit)
            );
        }

        return repository.findTopByPlayTime(config.topUsersQueryLimit)
                .orTimeout(config.topUsersQueryTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(users -> {
                    cache.update(users);
                    return slice(users, displayLimit);
                });
    }

    public void invalidateCache() {
        cache.invalidate();
    }

    private static List<PlayTimeUser> slice(List<PlayTimeUser> users, int limit) {
        return users.size() <= limit ? users : users.subList(0, limit);
    }
}


