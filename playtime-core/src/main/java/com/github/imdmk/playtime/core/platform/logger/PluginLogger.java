package com.github.imdmk.playtime.core.platform.logger;

import org.intellij.lang.annotations.PrintFormat;


public interface PluginLogger {

    void info(String message);
    void info(@PrintFormat String message, Object... args);

    void warn(String message);
    void warn(@PrintFormat String message, Object... args);
    void warn(Throwable throwable);
    void warn(Throwable throwable, @PrintFormat String message, Object... args);

    void error(String message);
    void error(@PrintFormat String message, Object... args);
    void error(Throwable throwable);
    void error(Throwable throwable, @PrintFormat String message, Object... args);
    void error(Throwable throwable, String message);

}
