package com.github.imdmk.playtime.config;

import eu.okaeri.configs.exception.OkaeriException;
import org.jetbrains.annotations.NotNull;

final class ConfigLifecycle {

    void initialize(@NotNull ConfigSection config) {
        config.saveDefaults();
        load(config);
    }

    void load(@NotNull ConfigSection config) {
        try {
            config.load(true);
        } catch (OkaeriException e) {
            throw new ConfigAccessException("Failed to load config: " + config.getClass().getSimpleName(), e);
        }
    }

    void save(@NotNull ConfigSection config) {
        try {
            config.save();
        } catch (OkaeriException e) {
            throw new ConfigAccessException("Failed to save config: " + config.getClass().getSimpleName(), e);
        }
    }
}

