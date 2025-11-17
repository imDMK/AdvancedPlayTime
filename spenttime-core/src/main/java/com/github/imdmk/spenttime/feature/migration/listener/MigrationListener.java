package com.github.imdmk.spenttime.feature.migration.listener;

import com.github.imdmk.spenttime.feature.migration.MigrationResult;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public interface MigrationListener {

    default void onStart(int total) {}

    default void onSuccess(@NotNull OfflinePlayer player) {}

    default void onFailed(@NotNull OfflinePlayer player, @NotNull Throwable throwable) {}

    default void onEnd(@NotNull MigrationResult result) {}

}
