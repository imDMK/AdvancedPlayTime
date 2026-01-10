package com.github.imdmk.playtime.user.top;

import com.github.imdmk.playtime.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

record CachedLeaderboard(
        @NotNull List<User> users,
        int limit,
        @NotNull Instant loadedAt
) {

    CachedLeaderboard {
        users = List.copyOf(users);
    }

    boolean isUsable(int requestedLimit, @NotNull Duration expireAfter, @NotNull Instant now) {
        if (this.limit < requestedLimit) {
            return false;
        }

        if (expireAfter.isZero() || expireAfter.isNegative()) {
            return true;
        }

        final Instant expiresAt = this.loadedAt.plus(expireAfter);
        return expiresAt.isAfter(now);
    }

    @NotNull
    @Override
    @Unmodifiable
    public List<User> users() {
        return users;
    }
}
