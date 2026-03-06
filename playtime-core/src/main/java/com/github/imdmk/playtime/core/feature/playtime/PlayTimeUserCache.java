package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeShutdownEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service(priority = ComponentPriority.LOWEST)
final class PlayTimeUserCache {

    private final Map<UUID, PlayTimeUser> cache = new ConcurrentHashMap<>();

    public void put(UUID uuid, PlayTimeUser user) {
        cache.put(uuid, user);
    }

    public Optional<PlayTimeUser> get(UUID uuid) {
        return Optional.ofNullable(cache.get(uuid));
    }

    void remove(UUID uuid) {
        cache.remove(uuid);
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        cache.clear();
    }
}

