package com.github.imdmk.playtime.feature.migration.provider;

import com.github.imdmk.playtime.shared.validate.Validator;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class BukkitPlayerProvider implements PlayerProvider {

    private final Server server;

    @Inject
    public BukkitPlayerProvider(@NotNull Server server) {
        this.server = Validator.notNull(server, "server cannot be null");
    }

    @Override
    public @NotNull @Unmodifiable Collection<OfflinePlayer> getAllPlayers() {
        return List.copyOf(Arrays.asList(server.getOfflinePlayers()));
    }
}
