package com.github.imdmk.playtime.user.top;

import com.github.imdmk.playtime.config.ConfigSection;
import com.github.imdmk.playtime.injector.annotations.ConfigFile;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@ConfigFile
public final class TopUsersCacheConfig extends ConfigSection {

    @Comment({
            "#",
            "# Maximum number of top users fetched from the storage when building the leaderboard.",
            "# This defines how many players will appear in the Top Playtime ranking.",
            "#",
            "# Notes:",
            "#  - Querying more players increases DB load but gives smoother scrolling if GUI supports it.",
            "#  - 30–50 is usually optimal for most servers.",
            "#"
    })
    public int topUsersQueryLimit = 30;

    @Comment({
            "#",
            "# Duration after which the cached Top Users leaderboard expires.",
            "#",
            "# How it works:",
            "#  - When the first player opens /playtime top (or any top-playtime GUI/command),",
            "#    the plugin loads top users from the DB and caches the result.",
            "#  - Subsequent calls read the cache instantly (no DB hit).",
            "#  - After this duration passes, the cache auto-invalidates and the next request reloads it.",
            "#",
            "# Recommended:",
            "#  5-15m depending on server size.",
            "#"
    })
    public Duration topUsersCacheExpireAfter = Duration.ofMinutes(10);

    @Comment({
            "#",
            "# Maximum allowed time to execute the database query that loads the Top Users.",
            "#",
            "# If the DB does not return results within this time window,",
            "# the fetch attempt is cancelled and the plugin will return an error message",
            "# instead of blocking the main thread or freezing the server.",
            "#",
            "# Notes:",
            "#  - Keep this low. 2–5 seconds is a safe range.",
            "#  - Protects the server from slow/no-response database calls.",
            "#"
    })
    public Duration topUsersQueryTimeout = Duration.ofSeconds(3);

    @Override
    public @NotNull OkaeriSerdesPack serdesPack() {
        return registry -> {};
    }

    @Override
    public @NotNull String fileName() {
        return "leaderboard.yml";
    }
}
