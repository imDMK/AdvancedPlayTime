package com.github.imdmk.playtime.feature.migration;

import com.github.imdmk.playtime.shared.config.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class MigrationConfig extends ConfigSection {

    @Comment({
            "#",
            "# Enables a one-time automatic migration on the first plugin startup.",
            "#",
            "# How it works:",
            "#  - When set to true, the plugin will perform a full server migration on startup.",
            "#  - After the migration completes successfully, the plugin will automatically set this value to false",
            "#    to prevent running the same migration again on the next startup.",
            "#  - Set this back to true manually only if you know what you are doing and want to re-run the initial migration.",
            "#"
    })
    public boolean initialServerMigrationEnabled = true;

    @Comment({
            "#",
            "# Maximum allowed execution time for a single migration task.",
            "#",
            "# If a specific migration step (e.g. processing a batch of players) exceeds this duration,",
            "# it will be treated as timed-out and can be cancelled or failed.",
            "#"
    })
    public Duration migrationTaskTimeout = Duration.ofSeconds(5);

    @Comment({
            "#",
            "# Global timeout for the entire migration process.",
            "#",
            "# This is a hard upper limit for all migration tasks combined. If the full migration does not finish",
            "# within this time window, the process will be considered failed or aborted.",
            "#"
    })
    public Duration migrationGlobalTimeout = Duration.ofMinutes(2);

    @Comment({
            "#",
            "# Keep-alive interval for long-running migrations.",
            "#",
            "# Used to periodically signal that the migration is still active and progressing,",
            "# preventing it from being treated as stalled when processing large datasets.",
            "#"
    })
    public Duration migrationKeepAliveInterval = Duration.ofMinutes(1);

    @Comment({
            "#",
            "# Maximum number of migration tasks that can run concurrently.",
            "#",
            "# This controls how many player/segment migrations can be processed in parallel.",
            "#",
            "# Recommendations:",
            "#  - Low values (1–2) are safer for small or heavily loaded servers.",
            "#  - Higher values (4+) speed up migration but may increase CPU/IO usage.",
            "#"
    })
    public int migrationMaxConcurrency = 3;

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            // No custom serializers required for this config.
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "migrationConfig.yml";
    }
}
