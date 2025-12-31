package com.github.imdmk.playtime.user.cache;

import com.github.imdmk.playtime.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface UserCache {

    void cacheUser(@NotNull User user);

    void invalidateUser(@NotNull User user);

    void invalidateByUuid(@NotNull UUID uuid);

    void invalidateByName(@NotNull String name);

    Optional<User> getUserByUuid(@NotNull UUID uuid);

    Optional<User> getUserByName(@NotNull String name);

    void updateUserNameMapping(@NotNull User user, @NotNull String oldName);

    void forEachUser(@NotNull Consumer<User> action);

    Collection<User> getCache();

    void invalidateAll();
}
