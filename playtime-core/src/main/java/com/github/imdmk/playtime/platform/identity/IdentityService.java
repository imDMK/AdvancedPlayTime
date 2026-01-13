package com.github.imdmk.playtime.platform.identity;

import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

@Service(priority = ComponentPriority.LOW)
public final class IdentityService {

    private static final String UNKNOWN_PLAYER_NAME = "Unknown";

    private final Server server;
    private final IdentityCache cache;

    public IdentityService(@NotNull Server server, @NotNull IdentityCache cache) {
        this.server = server;
        this.cache = cache;
    }

    public String resolvePlayerName(@NotNull UUID playerId) {
        final Optional<String> cached = cache.getNameByUuid(playerId);
        return cached.orElseGet(() -> Optional.ofNullable(server.getPlayer(playerId))
                .map(Player::getName)
                .orElse(UNKNOWN_PLAYER_NAME));

    }
}
