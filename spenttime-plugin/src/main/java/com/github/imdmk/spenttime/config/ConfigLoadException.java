package com.github.imdmk.spenttime.config;

import org.jetbrains.annotations.NotNull;

public final class ConfigLoadException extends RuntimeException {

    public ConfigLoadException(@NotNull Throwable cause) {
        super("Failed to load configuration", cause);
    }

    public ConfigLoadException(@NotNull String message) {
        super(message);
    }

    public ConfigLoadException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}