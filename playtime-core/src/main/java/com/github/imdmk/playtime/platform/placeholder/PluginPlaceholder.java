package com.github.imdmk.playtime.platform.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PluginPlaceholder {

    @NotNull String identifier();

    default @Nullable String onRequest(@NotNull Player player, @NotNull String params) {
        return null;
    }

    default @Nullable String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        return null;
    }
}
