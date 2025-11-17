package com.github.imdmk.spenttime.feature.migration.listener;

import com.github.imdmk.spenttime.feature.migration.MigrationConfig;
import com.github.imdmk.spenttime.feature.migration.MigrationResult;
import com.github.imdmk.spenttime.shared.Validator;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

public final class ConfigMigrationListener implements MigrationListener {

    private final MigrationConfig config;

    public ConfigMigrationListener(@NotNull MigrationConfig config) {
        this.config = Validator.notNull(config, "config cannot be null");
    }

    @Override
    public void onEnd(@NotNull MigrationResult result) {
        config.firstSetupMigrationEnabled = false;
        config.save();
    }
}
