package com.github.imdmk.spenttime;

import org.jetbrains.annotations.NotNull;

/**
 * Static access point for the {@link SpentTimeApi}.
 * <p>
 * Thread-safe: publication via synchronized register/unregister and a volatile reference.
 */
public final class SpentTimeApiProvider {

    private static volatile SpentTimeApi API; // visibility across threads

    private SpentTimeApiProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Returns the registered {@link SpentTimeApi}.
     *
     * @return the registered API
     * @throws IllegalStateException if the API is not registered
     */
    public static @NotNull SpentTimeApi get() {
        SpentTimeApi api = API;
        if (api == null) {
            throw new IllegalStateException("SpentTimeApi is not registered.");
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
     * Registers the {@link SpentTimeApi} instance.
     *
     * @param api the API instance to register
     * @throws IllegalStateException if already registered
     */
    static synchronized void register(@NotNull SpentTimeApi api) {
        if (API != null) {
            throw new IllegalStateException("SpentTimeApi is already registered.");
        }
        API = api;
    }

    /**
     * Forces registration of the {@link SpentTimeApi} instance.
     * <p>
     * Intended for tests/bootstrap only; overwrites any existing instance.
     */
    static synchronized void forceRegister(@NotNull SpentTimeApi api) {
        API = api;
    }

    /**
     * Unregisters the {@link SpentTimeApi}.
     *
     * @throws IllegalStateException if no API was registered
     */
    static synchronized void unregister() {
        if (API == null) {
            throw new IllegalStateException("SpentTimeApi is not registered.");
        }
        API = null;
    }
}
