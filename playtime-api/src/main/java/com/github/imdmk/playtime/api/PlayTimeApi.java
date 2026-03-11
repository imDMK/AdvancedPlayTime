package com.github.imdmk.playtime.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayTimeApi {

    CompletableFuture<PlayTime> getTime(UUID uuid);
    CompletableFuture<Void> setTime(UUID uuid, PlayTime time);

}
