package com.github.imdmk.playtime.core.feature.playtime.repository;

import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayTimeUserRepository {

    CompletableFuture<PlayTimeUser> findByUuid(UUID uuid);
    CompletableFuture<PlayTimeUser> findByName(String name);

    CompletableFuture<List<PlayTimeUser>> findTopByPlayTime(int limit);
    CompletableFuture<List<PlayTimeUser>> findAll();

    CompletableFuture<Boolean> deleteByUuid(UUID uuid);

    CompletableFuture<PlayTimeUser> save(PlayTimeUser user);
}
