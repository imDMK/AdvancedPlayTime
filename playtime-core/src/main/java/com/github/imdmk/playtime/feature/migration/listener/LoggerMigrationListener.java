package com.github.imdmk.playtime.feature.migration.listener;

import com.github.imdmk.playtime.feature.migration.MigrationResult;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.Validator;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class LoggerMigrationListener implements MigrationListener {

    private final PluginLogger logger;

    private volatile int completed;
    private volatile int total;

    public LoggerMigrationListener(@NotNull PluginLogger logger) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
    }

    @Override
    public void onStart(int total) {
        this.total = total;
        this.completed = 0;
        logger.info("Starting first-time migration of %d players...", total);
    }

    @Override
    public void onSuccess(@NotNull OfflinePlayer player) {
        incrementAndLogProgress();
    }

    @Override
    public void onFailed(@NotNull OfflinePlayer player, @NotNull Throwable throwable) {
        logger.warn("Migration failed for %s: %s", player.getUniqueId(), throwable.getMessage());
        incrementAndLogProgress();
    }

    @Override
    public void onEnd(@NotNull MigrationResult result) {
        logger.info("Migration ended: success=%d, failed=%d, took=%sms", result.successful(), result.failed(), result.took().toMillis());
    }

    private void incrementAndLogProgress() {
        int done = completed + 1;
        int total = Math.max(1, this.total);
        int percent = (int) ((done * 100L) / total);

        if (percent % 5 == 0 || done == total) {
            logger.info("Migration progress: %d%% (%d/%d)", percent, done, total);
        }
    }
}
