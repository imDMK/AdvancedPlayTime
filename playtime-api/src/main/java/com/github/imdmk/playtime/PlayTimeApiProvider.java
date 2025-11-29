package com.github.imdmk.playtime;

import org.jetbrains.annotations.NotNull;

/**
 * Static access point for the {@link PlayTimeApi}.
 * <p>
 * Thread-safe: publication via synchronized register/unregister and a volatile reference.
 */
public final class PlayTimeApiProvider {

    private static volatile PlayTimeApi API; // visibility across threads

    private PlayTimeApiProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Returns the registered {@link PlayTimeApi}.
     *
     * @return the registered API
     * @throws IllegalStateException if the API is not registered
     */
    public static @NotNull PlayTimeApi get() {
        PlayTimeApi api = API;
        if (api == null) {
            throw new IllegalStateException("PlayTimeAPI is not registered.");
        }
        return api;
    }

    /**
     * Checks if the API is registered
     *
     * @return {@code true} if the API is registered.
     */
    public static boolean isRegistered() {
        return API != null;
    }

    /**
     * Registers the {@link PlayTimeApi} instance.
     *
     * @param api the API instance to register
     * @throws IllegalStateException if already registered
     */
    static synchronized void register(@NotNull PlayTimeApi api) {
        if (API != null) {
            throw new IllegalStateException("PlayTimeAPI is already registered.");
        }
        API = api;
    }

    /**
     * Forces registration of the {@link PlayTimeApi} instance.
     * <p>
     * Intended for tests/bootstrap only; overwrites any existing instance.
     */
    static synchronized void forceRegister(@NotNull PlayTimeApi api) {
        API = api;
    }

    /**
     * Unregisters the {@link PlayTimeApi}.
     *
     * @throws IllegalStateException if no API was registered
     */
    static synchronized void unregister() {
        if (API == null) {
            throw new IllegalStateException("PlayTimeAPI is not registered.");
        }
        API = null;
    }
}
