package com.github.imdmk.playtime;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public final class PlayTimeApiProvider {

    private static final AtomicReference<PlayTimeApi> API = new AtomicReference<>();

    private PlayTimeApiProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    @NotNull
    public static PlayTimeApi get() {
        final PlayTimeApi api = API.get();
        if (api == null) {
            throw new IllegalStateException("PlayTimeAPI is not registered.");
        }
        return api;
    }

    public static boolean isRegistered() {
        return API.get() != null;
    }

    static void register(@NotNull PlayTimeApi api) {
        if (!API.compareAndSet(null, api)) {
            throw new IllegalStateException("PlayTimeAPI is already registered.");
        }
    }

    static void unregister() {
        if (!API.compareAndSet(API.get(), null)) {
            throw new IllegalStateException("PlayTimeAPI is not registered.");
        }
    }
}