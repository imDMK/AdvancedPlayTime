package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.api.PlayTimeApi;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayTimeApiAdapter implements PlayTimeApi {

    private final PlayTimeUserService userService;

    @Inject
    PlayTimeApiAdapter(PlayTimeUserService userService) {
        this.userService = userService;
    }

    @Override
    public CompletableFuture<PlayTime> getTime(UUID uuid) {
        return userService.getPlayTime(uuid);
    }

    @Override
    public CompletableFuture<Void> setTime(UUID uuid, PlayTime time) {
        return userService.setPlayTime(uuid, time);
    }

    @Override
    public CompletableFuture<Void> addTime(UUID uuid, PlayTime delta) {
        return userService.addPlayTime(uuid, delta);
    }

    @Override
    public CompletableFuture<Void> resetTime(UUID uuid) {
        return userService.setPlayTime(uuid, PlayTime.ZERO);
    }
}
