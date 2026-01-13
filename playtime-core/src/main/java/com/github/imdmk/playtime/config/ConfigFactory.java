package com.github.imdmk.playtime.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.exception.OkaeriException;
import org.jetbrains.annotations.NotNull;

final class ConfigFactory {

    <T extends ConfigSection> @NotNull T instantiate(@NotNull Class<T> type) {
        try {
            return ConfigManager.create(type);
        } catch (OkaeriException e) {
            throw new ConfigCreateException(
                    "Failed to instantiate config: " + type.getName(), e
            );
        }
    }
}

