package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service(priority = ComponentPriority.LOWEST)
final class PlayTimeUserCache {

    private final Map<UUID, PlayTimeUser> cache = new ConcurrentHashMap<>();

    public void put(@NotNull UUID uuid, @NotNull PlayTimeUser user) {
        cache.put(uuid, user);
    }

    public Optional<PlayTimeUser> get(@NotNull UUID uuid) {
        return Optional.ofNullable(cache.get(uuid));
    }

    void remove(@NotNull UUID uuid) {
        cache.remove(uuid);
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        cache.clear();
    }
}

