package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.PlayTimeApi;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayTimeApiAdapter implements PlayTimeApi {

    private final PlayTimeUserService userService;

    @Inject
    PlayTimeApiAdapter(@NotNull PlayTimeUserService userService) {
        this.userService = userService;
    }

    @Override
    public CompletableFuture<PlayTime> getTime(@NotNull UUID uuid) {
        return userService.getPlayTime(uuid);
    }

    @Override
    public CompletableFuture<Void> setTime(@NotNull UUID uuid, @NotNull PlayTime time) {
        return userService.setPlayTime(uuid, time);
    }

    @Override
    public CompletableFuture<Void> addTime(@NotNull UUID uuid, @NotNull PlayTime delta) {
        return userService.addPlayTime(uuid, delta);
    }

    @Override
    public CompletableFuture<Void> resetTime(@NotNull UUID uuid) {
        return userService.setPlayTime(uuid, PlayTime.ZERO);
    }
}
