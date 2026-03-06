package com.github.imdmk.playtime.core.feature.playtime.top;

import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;
import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service(priority = ComponentPriority.LOW)
final class PlayTimeTopCache {

    private final AtomicReference<CachedPlayTimeTop> snapshot = new AtomicReference<>();

    Optional<CachedPlayTimeTop> get() {
        return Optional.ofNullable(snapshot.get());
    }

    void update(List<PlayTimeUser> users) {
        snapshot.set(new CachedPlayTimeTop(
                List.copyOf(users),
                Instant.now()
        ));
    }

    void invalidate() {
        snapshot.set(null);
    }
}

