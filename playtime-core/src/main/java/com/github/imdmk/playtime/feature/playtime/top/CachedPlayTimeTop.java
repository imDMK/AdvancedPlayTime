package com.github.imdmk.playtime.feature.playtime.top;

import com.github.imdmk.playtime.feature.playtime.PlayTimeUser;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

record CachedPlayTimeTop(
        @NotNull List<PlayTimeUser> users,
        @NotNull Instant loadedAt
) {
    boolean isExpired(@NotNull Duration ttl, @NotNull Instant now) {
        return ttl.isPositive() && loadedAt.plus(ttl).isBefore(now);
    }
}

