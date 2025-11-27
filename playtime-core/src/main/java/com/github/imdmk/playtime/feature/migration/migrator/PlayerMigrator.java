package com.github.imdmk.playtime.feature.migration.migrator;

import com.github.imdmk.playtime.user.User;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface PlayerMigrator {

    CompletableFuture<User> migrate(@NotNull OfflinePlayer player);
}
