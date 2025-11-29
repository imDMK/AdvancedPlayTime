package com.github.imdmk.playtime.feature.migration.runner;

import com.github.imdmk.playtime.feature.migration.MigrationConfig;
import com.github.imdmk.playtime.feature.migration.MigrationResult;
import com.github.imdmk.playtime.feature.migration.listener.ConfigMigrationListener;
import com.github.imdmk.playtime.feature.migration.listener.LoggerMigrationListener;
import com.github.imdmk.playtime.feature.migration.listener.MigrationListener;
import com.github.imdmk.playtime.feature.migration.migrator.PlayerMigrator;
import com.github.imdmk.playtime.feature.migration.provider.PlayerProvider;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.Validator;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.List;

public final class BlockingMigrationRunner implements MigrationRunner<MigrationResult> {

    private final PluginLogger logger;
    private final MigrationConfig config;
    private final PlayerProvider provider;
    private final PlayerMigrator migrator;

    @Inject
    public BlockingMigrationRunner(
            @NotNull PluginLogger logger,
            @NotNull MigrationConfig config,
            @NotNull PlayerProvider provider,
            @NotNull PlayerMigrator migrator) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.config = Validator.notNull(config, "config cannot be null");
        this.provider = Validator.notNull(provider, "provider cannot be null");
        this.migrator = Validator.notNull(migrator, "migrator cannot be null");
    }

    @Override
    public MigrationResult execute() {
        var runner = new MigrationRunnerImpl(config, provider, migrator, listeners());
        return runner.execute();
    }

    @Override
    public List<MigrationListener> listeners() {
        return List.of(
                new ConfigMigrationListener(config),
                new LoggerMigrationListener(logger)
        );
    }
}
