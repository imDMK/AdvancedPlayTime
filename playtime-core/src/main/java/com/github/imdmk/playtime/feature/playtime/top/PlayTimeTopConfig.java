package com.github.imdmk.playtime.feature.playtime.top;

import com.github.imdmk.playtime.config.ConfigSection;
import com.github.imdmk.playtime.injector.annotations.ConfigFile;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

import java.time.Duration;

@ConfigFile
public final class PlayTimeTopConfig extends ConfigSection {

    @Comment({
            "#",
            "# How many users are fetched from the database when building the leaderboard snapshot.",
            "#",
            "# This affects DATABASE LOAD only.",
            "# The fetched list is cached and reused for GUI, commands and pagination.",
            "#",
            "# Recommended:",
            "#  - Small servers: 20–30",
            "#  - Medium servers: 30–50",
            "#  - Large servers: 50–100",
            "#"
    })
    public int topUsersQueryLimit = 50;

    @Comment({
            "#",
            "# How many users are displayed in the /playtime top GUI.",
            "#",
            "# This does NOT affect database queries.",
            "# It only slices the cached leaderboard snapshot.",
            "#"
    })
    public int topUsersGuiLimit = 10;

    @Comment({
            "#",
            "# How long the cached leaderboard snapshot stays valid.",
            "#",
            "# After this time expires, the next request will reload data from the database.",
            "#",
            "# Recommended:",
            "#  - 5–15 minutes",
            "#"
    })
    public Duration topUsersCacheExpireAfter = Duration.ofMinutes(10);

    @Comment({
            "#",
            "# Maximum time allowed for the database query that loads the leaderboard.",
            "#",
            "# If exceeded, the operation fails instead of blocking the server.",
            "#",
            "# Recommended:",
            "#  - 2–5 seconds",
            "#"
    })
    public Duration topUsersQueryTimeout = Duration.ofSeconds(3);

    @Override
    public OkaeriSerdesPack serdesPack() {
        return registry -> {};
    }

    @Override
    public String fileName() {
        return "playtime-top.yml";
    }
}
