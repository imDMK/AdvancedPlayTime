package com.github.imdmk.playtime.user;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserService {

    Optional<User> findCachedByUuid(@NotNull UUID uuid);
    Optional<User> findCachedByName(@NotNull String name);
    Collection<User> getCachedUsers();

    CompletableFuture<Optional<User>> findByUuid(@NotNull UUID uuid);
    CompletableFuture<Optional<User>> findByName(@NotNull String name);

    CompletableFuture<UserDeleteResult> deleteByUuid(@NotNull UUID uuid);
    CompletableFuture<UserDeleteResult> deleteByName(@NotNull String name);

    CompletableFuture<User> save(@NotNull User user, @NotNull UserSaveReason reason);

    CompletableFuture<List<User>> findTopByPlayTime(int limit);
}
