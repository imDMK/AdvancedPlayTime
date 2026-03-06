package com.github.imdmk.playtime.core.feature.playtime;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayTimeUserRepository {

    CompletableFuture<Optional<PlayTimeUser>> findByUuid(UUID uuid);
    CompletableFuture<List<PlayTimeUser>> findTopByPlayTime(int limit);
    CompletableFuture<List<PlayTimeUser>> findAll();

    CompletableFuture<Boolean> deleteByUuid(UUID uuid);

    CompletableFuture<Void> save(PlayTimeUser user);
}
