package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.platform.playtime.PlayTimeAdapter;
import org.bukkit.Server;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service(priority = ComponentPriority.HIGH)
final class PlayTimeQueryService {

    private final Server server;
    private final PlayTimeAdapter playTimeAdapter;
    private final PlayTimeUserService userService;

    @Inject
    PlayTimeQueryService(
            Server server,
            PlayTimeAdapter playTimeAdapter,
            PlayTimeUserService userService
    ) {
        this.server = server;
        this.playTimeAdapter = playTimeAdapter;
        this.userService = userService;
    }

    public CompletableFuture<PlayTime> getCurrentPlayTime(UUID uuid) {
        return Optional.ofNullable(server.getPlayer(uuid))
                .map(online -> CompletableFuture.completedFuture(playTimeAdapter.read(online)))
                .orElseGet(() -> userService.getPlayTime(uuid));
    }
}
