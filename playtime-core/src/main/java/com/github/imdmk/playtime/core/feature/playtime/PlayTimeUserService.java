package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
final class PlayTimeUserService {

    private final PlayTimeUserCache cache;
    private final PlayTimeUserRepository repository;

    @Inject
    PlayTimeUserService(PlayTimeUserCache cache, PlayTimeUserRepository repository) {
        this.cache = cache;
        this.repository = repository;
    }

    CompletableFuture<PlayTimeUser> getOrCreate(
            UUID uuid,
            PlayTime initialPlayTime
    ) {
        return cache.get(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> repository.findByUuid(uuid)
                .thenCompose(optional -> {
                    if (optional.isPresent()) {
                        return CompletableFuture.completedFuture(optional.get());
                    }

                     PlayTimeUser user = new PlayTimeUser(uuid, initialPlayTime);
                    return repository.save(user)
                            .thenApply(v -> user);
                }));

    }

    CompletableFuture<PlayTimeUser> getOrCreate(UUID uuid) {
        return getOrCreate(uuid, PlayTime.ZERO);
    }

    CompletableFuture<PlayTime> getPlayTime(UUID uuid) {
        return getOrCreate(uuid)
                .thenApply(PlayTimeUser::getPlayTime);
    }

    CompletableFuture<Void> setPlayTime(
            UUID uuid,
            PlayTime playTime
    ) {
        return getOrCreate(uuid)
                .thenCompose(user -> {
                    user.setPlayTime(playTime);
                    return repository.save(user);
                });
    }

    CompletableFuture<Void> addPlayTime(
            UUID uuid,
            PlayTime delta
    ) {
        return getOrCreate(uuid)
                .thenCompose(user -> {
                    user.setPlayTime(user.getPlayTime().plus(delta));
                    return repository.save(user);
                });
    }

    CompletableFuture<Boolean> delete(UUID uuid) {
        return repository.deleteByUuid(uuid);
    }
}

