package com.github.imdmk.playtime.shared.config;

import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.Validator;
import eu.okaeri.configs.exception.OkaeriException;
import org.jetbrains.annotations.NotNull;

final class ConfigLifecycle {

    private final PluginLogger logger;

    ConfigLifecycle(@NotNull PluginLogger logger) {
        this.logger = Validator.notNull(logger, "logger");
    }

    void initialize(@NotNull ConfigSection config) {
        config.saveDefaults();
        load(config);
    }

    void load(@NotNull ConfigSection config) {
        try {
            config.load(true);
        } catch (OkaeriException e) {
            logger.error(e, "Failed to load config %s", config.getClass().getSimpleName());
            throw new ConfigAccessException(e);
        }
    }

    void save(@NotNull ConfigSection config) {
        try {
            config.save();
        } catch (Exception e) {
            logger.error(e, "Failed to save config %s", config.getClass().getSimpleName());
            throw new ConfigAccessException(e);
        }
    }
}

