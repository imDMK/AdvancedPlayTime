package com.github.imdmk.playtime.api;

import java.util.concurrent.atomic.AtomicReference;

public final class PlayTimeApiProvider {

    private static final AtomicReference<PlayTimeApi> API = new AtomicReference<>();

    private PlayTimeApiProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static PlayTimeApi get() {
        PlayTimeApi api = API.get();
        if (api == null) {
            throw new IllegalStateException("PlayTimeAPI is not registered.");
        }
        return api;
    }

    public static boolean isRegistered() {
        return API.get() != null;
    }

    public static void register(PlayTimeApi api) {
        if (!API.compareAndSet(null, api)) {
            throw new IllegalStateException("PlayTimeAPI is already registered.");
        }
    }

    public static void unregister() {
        if (API.getAndSet(null) == null) {
            throw new IllegalStateException("PlayTimeAPI is not registered.");
        }
    }
}