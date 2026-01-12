package com.github.imdmk.playtime.feature.playtime;

import org.jetbrains.annotations.NotNull;
import panda.std.reactive.Completable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

interface PlayTimeUserRepository {

    CompletableFuture<Optional<PlayTimeUser>> findByUuid(@NotNull UUID uuid);
    CompletableFuture<List<PlayTimeUser>> findAll();

    CompletableFuture<Boolean> deleteByUuid(@NotNull UUID uuid);

    CompletableFuture<Void> save(@NotNull PlayTimeUser user);
}
