package com.github.imdmk.playtime.core.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.exception.OkaeriException;


final class ConfigFactory {

    <T extends ConfigSection> T instantiate(Class<T> type) {
        try {
            return ConfigManager.create(type);
        } catch (OkaeriException e) {
            throw new ConfigCreateException(
                    "Failed to instantiate config: " + type.getName(), e
            );
        }
    }
}

