package com.github.imdmk.playtime.core.feature.playtime.top;

import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

record CachedPlayTimeTop(
        List<PlayTimeUser> users,
        Instant loadedAt
) {
    boolean isExpired(Duration ttl, Instant now) {
        return ttl.isPositive() && loadedAt.plus(ttl).isBefore(now);
    }
}

