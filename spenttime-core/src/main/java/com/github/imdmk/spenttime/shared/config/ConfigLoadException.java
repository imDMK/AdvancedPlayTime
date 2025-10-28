package com.github.imdmk.spenttime.shared.config;

import org.jetbrains.annotations.NotNull;

/**
 * Thrown to indicate that a configuration file failed to load or save.
 * <p>
 * Wraps exceptions raised by the OkaeriConfig framework or underlying I/O operations.
 * Used to propagate configuration-related errors as unchecked exceptions.
 * </p>
 */
public final class ConfigLoadException extends RuntimeException {

    /**
     * Creates a new {@link ConfigLoadException} with a default message and cause.
     *
     * @param cause the underlying exception that caused this failure
     */
    public ConfigLoadException(@NotNull Throwable cause) {
        super("Failed to load configuration", cause);
    }

    /**
     * Creates a new {@link ConfigLoadException} with a custom message.
     *
     * @param message the detail message describing the failure
     */
    public ConfigLoadException(@NotNull String message) {
        super(message);
    }

    /**
     * Creates a new {@link ConfigLoadException} with a custom message and cause.
     *
     * @param message the detail message describing the failure
     * @param cause   the underlying exception that caused this failure
     */
    public ConfigLoadException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
