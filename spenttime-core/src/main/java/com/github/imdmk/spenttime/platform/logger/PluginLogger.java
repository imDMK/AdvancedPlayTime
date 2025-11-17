package com.github.imdmk.spenttime.platform.logger;

import org.intellij.lang.annotations.PrintFormat;
import org.jetbrains.annotations.NotNull;

/**
 * Unified logging abstraction for the SpentTime plugin environment.
 *
 * <p>This interface defines a consistent logging API decoupled from the underlying
 * logging backend (e.g., Bukkit’s {@link java.util.logging.Logger}, SLF4J, or a custom logger).
 * It provides structured, formatted, and throwable-aware logging across multiple log levels.</p>
 *
 * <p><strong>Design goals:</strong></p>
 * <ul>
 *   <li>Consistent logging interface across all plugin components.</li>
 *   <li>Support for message formatting with {@link String#format} syntax.</li>
 *   <li>Convenient overloads for attaching {@link Throwable}s and stack traces.</li>
 *   <li>Simple to implement for different backends (e.g., {@link BukkitPluginLogger}).</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong> Implementations are expected to be thread-safe and safe
 * for concurrent use across async or scheduled tasks.</p>
 *
 * @see BukkitPluginLogger
 * @see java.util.logging.Logger
 */
public interface PluginLogger {

    /**
     * Logs a general informational message.
     *
     * @param message message to log (non-null)
     */
    void info(@NotNull String message);

    /**
     * Logs a formatted informational message.
     *
     * @param message format string using {@link String#format} syntax (non-null)
     * @param args    arguments to format (non-null)
     */
    void info(@NotNull @PrintFormat String message, @NotNull Object... args);

    /**
     * Logs a warning message, indicating a non-fatal issue.
     *
     * @param message warning message (non-null)
     */
    void warn(@NotNull String message);

    /**
     * Logs a warning caused by a {@link Throwable}, typically without rethrowing it.
     *
     * @param throwable the exception or error (non-null)
     */
    void warn(@NotNull Throwable throwable);

    /**
     * Logs a formatted warning message with an associated {@link Throwable}.
     *
     * @param throwable cause of the warning (non-null)
     * @param message   format string (non-null)
     * @param args      format arguments (non-null)
     */
    void warn(@NotNull Throwable throwable,
              @NotNull @PrintFormat String message,
              @NotNull Object... args);

    /**
     * Logs a formatted warning message.
     *
     * @param message format string (non-null)
     * @param args    format arguments (non-null)
     */
    void warn(@NotNull @PrintFormat String message, @NotNull Object... args);

    /**
     * Logs an error with a throwable stack trace.
     *
     * @param throwable exception or error to log (non-null)
     */
    void error(@NotNull Throwable throwable);

    /**
     * Logs an error message at the highest severity level.
     *
     * @param message message to log (non-null)
     */
    void error(@NotNull String message);

    /**
     * Logs a formatted error message.
     *
     * @param message format string (non-null)
     * @param args    format arguments (non-null)
     */
    void error(@NotNull @PrintFormat String message, @NotNull Object... args);

    /**
     * Logs an error message with the given {@link Throwable}.
     *
     * @param throwable cause of the error (non-null)
     * @param message   unformatted message text (non-null)
     */
    void error(@NotNull Throwable throwable, @NotNull String message);

    /**
     * Logs a formatted error message with the given {@link Throwable}.
     *
     * @param throwable cause of the error (non-null)
     * @param message   format string (non-null)
     * @param args      format arguments (non-null)
     */
    void error(@NotNull Throwable throwable,
               @NotNull @PrintFormat String message,
               @NotNull Object... args);
}
