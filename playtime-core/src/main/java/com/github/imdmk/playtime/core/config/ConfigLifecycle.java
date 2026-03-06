package com.github.imdmk.playtime.core.config;

import eu.okaeri.configs.exception.OkaeriException;

final class ConfigLifecycle {

    void initialize(ConfigSection config) {
        config.saveDefaults();
        load(config);
    }

    void load(ConfigSection config) {
        try {
            config.load(true);
        } catch (OkaeriException e) {
            throw new ConfigAccessException("Failed to load config: " + config.getClass().getSimpleName(), e);
        }
    }

    void save(ConfigSection config) {
        try {
            config.save();
        } catch (OkaeriException e) {
            throw new ConfigAccessException("Failed to save config: " + config.getClass().getSimpleName(), e);
        }
    }
}

