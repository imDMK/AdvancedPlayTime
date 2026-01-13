package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.platform.playtime.PlayTimeAdapter;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
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
            @NotNull Server server,
            @NotNull PlayTimeAdapter playTimeAdapter,
            @NotNull PlayTimeUserService userService
    ) {
        this.server = server;
        this.playTimeAdapter = playTimeAdapter;
        this.userService = userService;
    }

    public CompletableFuture<PlayTime> getCurrentPlayTime(@NotNull UUID uuid) {
        return Optional.ofNullable(server.getPlayer(uuid))
                .map(online -> CompletableFuture.completedFuture(playTimeAdapter.read(online)))
                .orElseGet(() -> userService.getPlayTime(uuid));
    }
}
