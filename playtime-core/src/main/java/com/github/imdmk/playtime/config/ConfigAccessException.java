package com.github.imdmk.playtime.config;

public class ConfigAccessException extends RuntimeException {

    public ConfigAccessException(String message) {
        super(message);
    }

    public ConfigAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigAccessException(Throwable cause) {
        super(cause);
    }
}

