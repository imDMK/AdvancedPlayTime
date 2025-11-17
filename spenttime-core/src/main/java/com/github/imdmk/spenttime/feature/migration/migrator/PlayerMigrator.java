package com.github.imdmk.spenttime.feature.migration.migrator;

import com.github.imdmk.spenttime.user.User;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface PlayerMigrator {

    CompletableFuture<User> migrate(@NotNull OfflinePlayer player);
}
