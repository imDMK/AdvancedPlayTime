package com.github.imdmk.playtime.user.top;

import com.github.imdmk.playtime.shared.validate.Validator;
import com.github.imdmk.playtime.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Immutable representation of a cached leaderboard snapshot.
 * <p>
 * Holds the ordered user list, the limit used during loading, and the timestamp
 * when the data was retrieved. Provides logic for determining whether the snapshot
 * is still valid for a given request.
 */
record CachedLeaderboard(
        @NotNull @Unmodifiable List<User> users,
        int limit,
        @NotNull Instant loadedAt
) {

    /**
     * Constructs a new leaderboard snapshot.
     * A defensive copy of the user list is created to ensure immutability.
     */
    CachedLeaderboard {
        Validator.notNull(users, "users");
        Validator.notNull(loadedAt, "loadedAt");
        users = List.copyOf(users);
    }

    /**
     * Determines whether this leaderboard is valid for the requested limit and expiration policy.
     *
     * @param requestedLimit limit requested by the caller
     * @param expireAfter    duration after which the snapshot becomes stale
     * @param now            current time reference
     * @return {@code true} if the leaderboard is fresh and large enough, otherwise {@code false}
     */
    boolean isUsable(int requestedLimit, @NotNull Duration expireAfter, @NotNull Instant now) {
        Validator.notNull(now, "now");

        if (this.limit < requestedLimit) {
            return false;
        }

        if (expireAfter.isZero() || expireAfter.isNegative()) {
            return true;
        }

        Instant expiresAt = this.loadedAt.plus(expireAfter);
        return expiresAt.isAfter(now);
    }

    /**
     * Returns the ordered user list.
     * This list is unmodifiable and safe to expose.
     *
     * @return immutable list of users
     */
    @Override
    @Unmodifiable
    public @NotNull List<User> users() {
        return users;
    }
}
