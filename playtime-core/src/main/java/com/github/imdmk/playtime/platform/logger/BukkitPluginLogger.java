package com.github.imdmk.playtime.platform.logger;

import com.github.imdmk.playtime.shared.Validator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bukkit-specific implementation of {@link PluginLogger} delegating to a standard
 * {@link java.util.logging.Logger} obtained from a Bukkit {@link Plugin}.
 *
 * <p>This class provides formatted and structured logging methods for common log levels
 * (INFO, WARNING, DEBUG, SEVERE) with support for formatted messages and throwable logging.</p>
 *
 * <p><strong>Design notes:</strong></p>
 * <ul>
 *   <li>Acts as a lightweight adapter to bridge the internal plugin logging interface with Bukkit’s logger.</li>
 *   <li>Formatting uses {@link String#format(Locale, String, Object...)} with {@link Locale#ROOT} to ensure locale safety.</li>
 *   <li>Supports overloaded methods for flexible log message creation, including formatted and exception-based variants.</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong> Delegates to the underlying {@link Logger}, which is thread-safe for concurrent use.</p>
 *
 * @see PluginLogger
 * @see Plugin#getLogger()
 * @see Logger
 */
public final class BukkitPluginLogger implements PluginLogger {

    /** Backing {@link java.util.logging.Logger} provided by Bukkit. */
    private final Logger logger;

    /**
     * Creates a new {@code BukkitPluginLogger} wrapping an existing {@link Logger}.
     *
     * @param logger non-null backing logger instance
     * @throws NullPointerException if {@code logger} is null
     */
    public BukkitPluginLogger(@NotNull Logger logger) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
    }

    /**
     * Creates a new {@code BukkitPluginLogger} bound to the given Bukkit {@link Plugin}.
     *
     * @param plugin non-null Bukkit plugin instance
     * @throws NullPointerException if {@code plugin} is null
     */
    public BukkitPluginLogger(@NotNull Plugin plugin) {
        this(plugin.getLogger());
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

    /**
     * Formats a message using {@link String#format(Locale, String, Object...)} with {@link Locale#ROOT}.
     *
     * @param message format string (non-null)
     * @param args    format arguments (non-null)
     * @return formatted message
     * @throws NullPointerException if {@code message} or {@code args} is null
     */
    private String format(@NotNull String message, @NotNull Object... args) {
        Validator.notNull(message, "message cannot be null");
        Validator.notNull(args, "args cannot be null");
        return String.format(Locale.ROOT, message, args);
    }
}
