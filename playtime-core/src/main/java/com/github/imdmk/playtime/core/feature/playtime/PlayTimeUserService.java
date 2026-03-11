package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.feature.playtime.repository.PlayTimeUserRepository;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public final class PlayTimeUserService {

    private final PlayTimeUserCache cache;
    private final PlayTimeUserRepository repository;

    @Inject
    PlayTimeUserService(
            PlayTimeUserCache cache,
            PlayTimeUserRepository repository
    ) {
        this.cache = cache;
        this.repository = repository;
    }

    public Optional<PlayTimeUser> getUser(UUID uuid) {
        return Optional.ofNullable(cache.getByUuid(uuid));
    }

    public Optional<PlayTimeUser> getUser(String name) {
        return Optional.ofNullable(cache.getByName(name));
    }

    public CompletableFuture<PlayTimeUser> getOrLoadUser(UUID uuid) {
        PlayTimeUser cached = cache.getByUuid(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return repository.findByUuid(uuid)
                .thenApply(user -> {
                    if (user != null) {
                        cache.put(user);
                    }
                    return user;
                });
    }

    public CompletableFuture<PlayTimeUser> getOrLoadUser(String name) {
        PlayTimeUser cached = cache.getByName(name);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return repository.findByName(name)
                .thenApply(user -> {
                    if (user != null) {
                        cache.put(user);
                    }
                    return user;
                });
    }

    public CompletableFuture<PlayTimeUser> createUser(UUID uuid, String name, PlayTime playTime) {
        PlayTimeUser user = new PlayTimeUser(uuid, name, playTime);
        cache.put(user);
        return repository.save(user);
    }

    public CompletableFuture<PlayTimeUser> saveUser(PlayTimeUser user) {
        cache.put(user);
        return repository.save(user);
    }

    public Collection<String> cachedNames() {
        return cache.names();
    }
}