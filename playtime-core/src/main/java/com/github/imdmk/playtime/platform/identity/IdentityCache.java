package com.github.imdmk.playtime.platform.identity;

import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public final class IdentityCache {

    private final Map<String, UUID> nameToUuid = new ConcurrentHashMap<>();

    public void update(@NotNull Player player) {
        nameToUuid.put(player.getName(), player.getUniqueId());
    }

    public void remove(@NotNull Player player) {
        nameToUuid.remove(player.getName());
    }

    public Optional<UUID> getUuidByName(@NotNull String name) {
        return Optional.ofNullable(nameToUuid.get(name));
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    public void shutdown() {
        nameToUuid.clear();
    }
}

