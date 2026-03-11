package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.api.PlayTimeApi;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayTimeApiAdapter implements PlayTimeApi {

    private final PlayTimeService playTimeService;
    private final PlayTimeUserService userService;

    @Inject
    PlayTimeApiAdapter(
            PlayTimeService playTimeService,
            PlayTimeUserService userService
    ) {
        this.playTimeService = playTimeService;
        this.userService = userService;
    }

    @Override
    public CompletableFuture<PlayTime> getTime(UUID uuid) {
        return userService.getOrLoadUser(uuid)
                .thenApply(PlayTimeUser::getPlayTime);
    }

    @Override
    public CompletableFuture<Void> setTime(UUID uuid, PlayTime time) {
        return userService.getOrLoadUser(uuid)
                .thenAccept(user -> playTimeService.setPlayTime(user, time));
    }
}
