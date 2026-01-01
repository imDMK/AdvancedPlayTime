package com.github.imdmk.playtime.user.repository;

import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserDeleteResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {

    CompletableFuture<Optional<User>> findByUuid(@NotNull UUID uuid);
    CompletableFuture<Optional<User>> findByName(@NotNull String name);
    CompletableFuture<List<User>> findAll();

    CompletableFuture<List<User>> findTopByPlayTime(long limit);

    CompletableFuture<UserDeleteResult> deleteByUuid(@NotNull UUID uuid);
    CompletableFuture<UserDeleteResult> deleteByName(@NotNull String name);

    CompletableFuture<User> save(@NotNull User user);
}
