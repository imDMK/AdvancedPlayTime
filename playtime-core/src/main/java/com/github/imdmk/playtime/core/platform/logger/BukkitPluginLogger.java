package com.github.imdmk.playtime.core.platform.logger;

import org.bukkit.plugin.Plugin;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BukkitPluginLogger implements PluginLogger {

    private final Logger logger;

    @Inject
    public BukkitPluginLogger(Plugin plugin) {
        this.logger = plugin.getLogger();
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Object... args) {
        logger.log(Level.INFO, format(message, args));
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }

    @Override
    public void warn(Throwable throwable) {
        logger.warning(throwable.getMessage());
    }

    @Override
    public void warn(Throwable throwable, String message, Object... args) {
        logger.log(Level.WARNING, format(message, args), throwable);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.log(Level.WARNING, format(message, args));
    }

    @Override
    public void error(Throwable throwable) {
        logger.severe(throwable.getMessage());
    }

    @Override
    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void error(String message, Object... args) {
        logger.log(Level.SEVERE, format(message, args));
    }

    @Override
    public void error(Throwable throwable, String message) {
        logger.log(Level.SEVERE, message, throwable);
    }

    @Override
    public void error(Throwable throwable, String message, Object... args) {
        logger.log(Level.SEVERE, format(message, args), throwable);
    }

    private String format(String message, Object... args) {
        return String.format(Locale.ROOT, message, args);
    }
}
