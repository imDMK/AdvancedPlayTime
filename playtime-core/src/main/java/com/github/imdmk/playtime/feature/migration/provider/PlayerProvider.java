package com.github.imdmk.playtime.feature.migration.provider;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PlayerProvider {

    @NotNull Collection<OfflinePlayer> getAllPlayers();

}