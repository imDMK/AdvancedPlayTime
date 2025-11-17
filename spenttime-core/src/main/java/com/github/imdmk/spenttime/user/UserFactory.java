package com.github.imdmk.spenttime.user;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Platform-to-domain adapter. Creates a new domain {@link User} from
 * a Bukkit {@link Player} on first join. No update logic here.
 */
public interface UserFactory {

    /**
     * Creates a fully initialized {@link User} from the given {@link Player}.
     * Implementations should set initial playtime using platform stats.
     */
    @NotNull User createFrom(@NotNull Player player);

    /**
     * Creates a fully initialized {@link User} from the given {@link OfflinePlayer}.
     * Implementations should set initial playtime using platform stats.
     */
    @NotNull User createFrom(@NotNull OfflinePlayer offlinePlayer);
}
