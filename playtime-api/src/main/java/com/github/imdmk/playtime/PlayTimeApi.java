package com.github.imdmk.playtime;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayTimeApi {

    CompletableFuture<PlayTime> getTime(@NotNull UUID uuid);

    CompletableFuture<Void> setTime(@NotNull UUID uuid, @NotNull PlayTime time);

    CompletableFuture<Void> resetTime(@NotNull UUID uuid);

}
