package com.github.imdmk.playtime.user.top;

import com.github.imdmk.playtime.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Cache abstraction for retrieving the top users by spent time.
 * <p>
 * Implementations are responsible for storing and serving leaderboard data,
 * including cache invalidation and optional limit-based slicing.
 */
public interface TopUsersCache {

    /**
     * Returns the cached or freshly loaded leaderboard using the default limit
     * defined in the cache configuration.
     *
     * @return future containing an ordered list of top users
     */
    CompletableFuture<List<User>> getTopByPlayTime();

    /**
     * Returns the cached or freshly loaded leaderboard limited to the given size.
     * Implementations may slice an existing cached leaderboard or trigger a reload
     * if the cache is stale or insufficient for the requested limit.
     *
     * @param limit maximum number of users to return
     * @return future containing an ordered list of top users
     */
    CompletableFuture<List<User>> getTopByPlayTime(int limit);

    /**
     * Invalidates all cached leaderboard data.
     * Next invocation of {@link #getTopByPlayTime()} will trigger a reload.
     */
    void invalidateAll();
}
