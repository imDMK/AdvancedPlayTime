package com.github.imdmk.playtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Factory and utilities for worker executor used by PlayTime.
 */
final class PlayTimeExecutorFactory {

    private static final String WORKER_THREAD_NAME = "AdvancedPlayTime-Worker";
    private static final Duration SHUTDOWN_TIMEOUT = Duration.ofSeconds(3);

    private PlayTimeExecutorFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Creates a dedicated single-threaded worker executor for asynchronous plugin operations.
     * The executor uses a named daemon thread ({@code PlayTime-Worker}).
     *
     * @return configured single-threaded executor service
     */
    static @NotNull ExecutorService newWorkerExecutor() {
        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable, WORKER_THREAD_NAME);
            thread.setDaemon(true);
            return thread;
        };

        return Executors.newSingleThreadExecutor(factory);
    }

    /**
     * Shuts down the given executor quietly, awaiting termination for a short period.
     * If it fails to terminate gracefully, all running tasks are forcibly cancelled.
     *
     * @param executor the executor to shut down, may be {@code null}
     */
    static void shutdownQuietly(@Nullable ExecutorService executor) {
        if (executor == null) {
            return;
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

