package com.github.imdmk.spenttime.shared.config;

import com.github.imdmk.spenttime.shared.time.DurationFormatStyle;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public final class PluginConfig extends ConfigSection {

    @Comment({
            "#",
            "# Determines how durations (playtime, cooldowns, timers) are formatted",
            "#",
            "# Available options:",
            "#   - COMPACT: short form like 3d 4h 10m",
            "#   - LONG: full names, e.g. 3 days 4 hours",
            "#   - LONG_WITH_AND: natural flow, e.g. 3 days and 4 hours",
            "#   - NATURAL: comma-separated, e.g. 3 days, 4 hours",
            "#"
    })
    public DurationFormatStyle durationFormatStyle = DurationFormatStyle.LONG_WITH_AND;

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String getFileName() {
        return "pluginConfig.yml";
    }
}
