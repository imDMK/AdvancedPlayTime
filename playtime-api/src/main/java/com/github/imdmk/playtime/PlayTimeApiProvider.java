package com.github.imdmk.playtime;

import org.jetbrains.annotations.NotNull;

public final class PlayTimeApiProvider {

    private static volatile PlayTimeApi API; // visibility across threads

    private PlayTimeApiProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static PlayTimeApi get() {
        final PlayTimeApi api = API;
        if (api == null) {
            throw new IllegalStateException("PlayTimeAPI is not registered.");
        }
        return api;
    }

    public static boolean isRegistered() {
        return API != null;
    }

    static synchronized void register(@NotNull PlayTimeApi api) {
        if (API != null) {
            throw new IllegalStateException("PlayTimeAPI is already registered.");
        }
        API = api;
    }

    static synchronized void forceRegister(@NotNull PlayTimeApi api) {
        API = api;
    }

    static synchronized void unregister() {
        if (API == null) {
            throw new IllegalStateException("PlayTimeAPI is not registered.");
        }
        API = null;
    }
}
