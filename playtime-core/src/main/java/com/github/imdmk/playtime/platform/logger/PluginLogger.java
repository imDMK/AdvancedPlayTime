package com.github.imdmk.playtime.platform.logger;

import org.intellij.lang.annotations.PrintFormat;
import org.jetbrains.annotations.NotNull;

public interface PluginLogger {

    void info(@NotNull String message);
    void info(@NotNull @PrintFormat String message, @NotNull Object... args);

    void warn(@NotNull String message);
    void warn(@NotNull @PrintFormat String message, @NotNull Object... args);
    void warn(@NotNull Throwable throwable);
    void warn(@NotNull Throwable throwable, @NotNull @PrintFormat String message, @NotNull Object... args);

    void error(@NotNull String message);
    void error(@NotNull @PrintFormat String message, @NotNull Object... args);
    void error(@NotNull Throwable throwable);
    void error(@NotNull Throwable throwable, @NotNull @PrintFormat String message, @NotNull Object... args);
    void error(@NotNull Throwable throwable, @NotNull String message);

}
