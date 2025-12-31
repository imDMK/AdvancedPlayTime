package com.github.imdmk.playtime.user.top;

import com.github.imdmk.playtime.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TopUsersCache {

    CompletableFuture<List<User>> getTopByPlayTime();

    CompletableFuture<List<User>> getTopByPlayTime(int limit);

    void invalidateAll();
}
