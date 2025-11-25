package com.github.imdmk.spenttime.user.cache;

import com.github.imdmk.spenttime.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Abstraction for caching {@link User} instances in memory.
 */
public interface UserCache {

    void cacheUser(@NotNull User user);

    void invalidateUser(@NotNull User user);

    void invalidateByUuid(@NotNull UUID uuid);

    void invalidateByName(@NotNull String name);

    @NotNull Optional<User> getUserByUuid(@NotNull UUID uuid);

    @NotNull Optional<User> getUserByName(@NotNull String name);

    void updateUserNameMapping(@NotNull User user, @NotNull String oldName);

    void forEachUser(@NotNull Consumer<User> action);

    @NotNull @Unmodifiable
    Collection<User> getCache();

    void invalidateAll();
}
