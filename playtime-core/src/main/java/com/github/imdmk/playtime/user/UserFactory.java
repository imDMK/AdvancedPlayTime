package com.github.imdmk.playtime.user;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface UserFactory {

    User createFrom(@NotNull Player player);

    User createFrom(@NotNull OfflinePlayer offlinePlayer);
}
