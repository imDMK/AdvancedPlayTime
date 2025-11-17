package com.github.imdmk.spenttime.feature.migration;

import com.github.imdmk.spenttime.shared.config.ConfigSection;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class MigrationConfig extends ConfigSection {

    public boolean firstSetupMigrationEnabled = true;

    public Duration taskTimeout = Duration.ofSeconds(5);

    public Duration globalTimeout = Duration.ofMinutes(2);

    public Duration keepAlive = Duration.ofMinutes(1);

    public int maxConcurrency = 4;

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String getFileName() {
        return "migrationConfig.yml";
    }
}
