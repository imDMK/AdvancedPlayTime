package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeShutdownEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service(priority = ComponentPriority.LOWEST)
final class PlayTimeUserCache {

    private final Map<UUID, PlayTimeUser> usersByUuid = new ConcurrentHashMap<>();
    private final Map<String, UUID> uuidByName = new ConcurrentHashMap<>();

    void put(PlayTimeUser user) {
        usersByUuid.put(user.getUuid(), user);
        uuidByName.put(user.getName(), user.getUuid());
    }

    @Nullable
    PlayTimeUser getByUuid(UUID uuid) {
        return usersByUuid.get(uuid);
    }

    @Nullable
    PlayTimeUser getByName(String name) {
        UUID uuid = uuidByName.get(name);
        return uuid != null ? usersByUuid.get(uuid) : null;
    }

    @Unmodifiable
    Collection<PlayTimeUser> values() {
        return Collections.unmodifiableCollection(usersByUuid.values());
    }

    @Unmodifiable
    Collection<String> names() {
        return Collections.unmodifiableCollection(uuidByName.keySet());
    }

    void remove(PlayTimeUser user) {
        usersByUuid.remove(user.getUuid());
        uuidByName.remove(user.getName());
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        usersByUuid.clear();
        uuidByName.clear();
    }
}
