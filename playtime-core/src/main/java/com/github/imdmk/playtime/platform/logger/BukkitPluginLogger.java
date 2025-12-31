package com.github.imdmk.playtime.platform.logger;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BukkitPluginLogger implements PluginLogger {

    /** Backing {@link java.util.logging.Logger} provided by Bukkit. */
    private final Logger logger;

    @Inject
    public BukkitPluginLogger(@NotNull Plugin plugin) {
        this.logger = plugin.getLogger();
    }

    @Override
    public void info(@NotNull String message) {
        logger.info(message);
    }

    @Override
    public void info(@NotNull String message, @NotNull Object... args) {
        logger.log(Level.INFO, format(message, args));
    }

    @Override
    public void warn(@NotNull String message) {
        logger.warning(message);
    }

    @Override
    public void warn(@NotNull Throwable throwable) {
        logger.warning(throwable.getMessage());
    }

    @Override
    public void warn(@NotNull Throwable throwable, @NotNull String message, @NotNull Object... args) {
        logger.log(Level.WARNING, format(message, args), throwable);
    }

    @Override
    public void warn(@NotNull String message, @NotNull Object... args) {
        logger.log(Level.WARNING, format(message, args));
    }

    @Override
    public void error(@NotNull Throwable throwable) {
        logger.severe(throwable.getMessage());
    }

    @Override
    public void error(@NotNull String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void error(@NotNull String message, @NotNull Object... args) {
        logger.log(Level.SEVERE, format(message, args));
    }

    @Override
    public void error(@NotNull Throwable throwable, @NotNull String message) {
        logger.log(Level.SEVERE, message, throwable);
    }

    @Override
    public void error(@NotNull Throwable throwable, @NotNull String message, @NotNull Object... args) {
        logger.log(Level.SEVERE, format(message, args), throwable);
    }

    private String format(String message, Object... args) {
        return String.format(Locale.ROOT, message, args);
    }
}
