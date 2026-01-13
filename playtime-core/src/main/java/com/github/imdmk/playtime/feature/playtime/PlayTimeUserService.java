package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.injector.annotations.Service;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
final class PlayTimeUserService {

    private final PlayTimeUserCache cache;
    private final PlayTimeUserRepository repository;

    @Inject
    PlayTimeUserService(@NotNull PlayTimeUserCache cache, @NotNull PlayTimeUserRepository repository) {
        this.cache = cache;
        this.repository = repository;
    }

    CompletableFuture<PlayTimeUser> getOrCreate(
            @NotNull UUID uuid,
            @NotNull PlayTime initialPlayTime
    ) {
        return cache.get(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> repository.findByUuid(uuid)
                .thenCompose(optional -> {
                    if (optional.isPresent()) {
                        return CompletableFuture.completedFuture(optional.get());
                    }

                    final PlayTimeUser user = new PlayTimeUser(uuid, initialPlayTime);
                    return repository.save(user)
                            .thenApply(v -> user);
                }));

    }

    CompletableFuture<PlayTimeUser> getOrCreate(@NotNull UUID uuid) {
        return getOrCreate(uuid, PlayTime.ZERO);
    }

    CompletableFuture<PlayTime> getPlayTime(@NotNull UUID uuid) {
        return getOrCreate(uuid)
                .thenApply(PlayTimeUser::getPlayTime);
    }

    CompletableFuture<Void> setPlayTime(
            @NotNull UUID uuid,
            @NotNull PlayTime playTime
    ) {
        return getOrCreate(uuid)
                .thenCompose(user -> {
                    user.setPlayTime(playTime);
                    return repository.save(user);
                });
    }

    CompletableFuture<Void> addPlayTime(
            @NotNull UUID uuid,
            @NotNull PlayTime delta
    ) {
        return getOrCreate(uuid)
                .thenCompose(user -> {
                    user.setPlayTime(user.getPlayTime().plus(delta));
                    return repository.save(user);
                });
    }

    CompletableFuture<Boolean> delete(@NotNull UUID uuid) {
        return repository.deleteByUuid(uuid);
    }
}

