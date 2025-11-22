package com.github.imdmk.spenttime.feature.migration.listener;

import com.github.imdmk.spenttime.feature.migration.MigrationResult;
import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.shared.Validator;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public final class LoggerMigrationListener implements MigrationListener {

    private static final String PREFIX = "[MIGRATION]";

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
        logger.info("%s Starting first-time migration of %d players...", PREFIX, total);
    }

    @Override
    public void onSuccess(@NotNull OfflinePlayer player) {
        incrementAndLogProgress();
    }

    @Override
    public void onFailed(@NotNull OfflinePlayer player, @NotNull Throwable throwable) {
        logger.warn("%s Migration failed for %s: %s", PREFIX, player.getUniqueId(), throwable.getMessage());
        incrementAndLogProgress();
    }

    @Override
    public void onEnd(@NotNull MigrationResult result) {
        logger.info("%s Migration ended: success=%d, failed=%d, took=%sms", PREFIX, result.successful(), result.failed(), result.took().toMillis());
    }

    private void incrementAndLogProgress() {
        int done = completed + 1;
        int total = Math.max(1, this.total);
        int percent = (int) ((done * 100L) / total);

        if (percent % 5 == 0 || done == total) {
            logger.info("%s Progress: %d%% (%d/%d)", PREFIX, percent, done, total);
        }
    }
}
