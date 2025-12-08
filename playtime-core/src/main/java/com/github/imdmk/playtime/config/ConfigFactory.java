package com.github.imdmk.playtime.config;

import com.github.imdmk.playtime.shared.validate.Validator;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.exception.OkaeriException;
import org.jetbrains.annotations.NotNull;

final class ConfigFactory {

    <T extends ConfigSection> @NotNull T instantiate(@NotNull Class<T> type) {
        Validator.notNull(type, "type");

        try {
            return ConfigManager.create(type);
        } catch (OkaeriException e) {
            throw new IllegalStateException(
                    "Failed to instantiate config: " + type.getName(), e
            );
        }
    }
}

