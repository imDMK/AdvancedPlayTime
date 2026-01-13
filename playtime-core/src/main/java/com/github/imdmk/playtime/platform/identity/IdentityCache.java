package com.github.imdmk.playtime.platform.identity;

import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service(priority = ComponentPriority.LOWEST)
public final class IdentityCache {

    private final Map<String, UUID> nameToUuid = new ConcurrentHashMap<>();
    private final Map<UUID, String> uuidToName = new ConcurrentHashMap<>();

    public void update(@NotNull Player player) {
        nameToUuid.put(player.getName(), player.getUniqueId());
        uuidToName.put(player.getUniqueId(), player.getName());
    }

    public void remove(@NotNull Player player) {
        nameToUuid.remove(player.getName());
        uuidToName.remove(player.getUniqueId());
    }

    public Optional<UUID> getUuidByName(@NotNull String name) {
        return Optional.ofNullable(nameToUuid.get(name));
    }

    public Optional<String> getNameByUuid(@NotNull UUID playerId) {
        return Optional.ofNullable(uuidToName.get(playerId));
    }

    @Unmodifiable
    public Set<UUID> getPlayerUuids() {
        return Set.copyOf(uuidToName.keySet());
    }

    @Unmodifiable
    public Set<String> getPlayerNames() {
        return Set.copyOf(nameToUuid.keySet());
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        nameToUuid.clear();
    }
}

