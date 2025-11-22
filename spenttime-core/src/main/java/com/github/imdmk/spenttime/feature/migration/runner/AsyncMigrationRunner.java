package com.github.imdmk.spenttime.feature.migration.runner;

import com.github.imdmk.spenttime.feature.migration.MigrationConfig;
import com.github.imdmk.spenttime.feature.migration.MigrationResult;
import com.github.imdmk.spenttime.feature.migration.listener.ConfigMigrationListener;
import com.github.imdmk.spenttime.feature.migration.listener.LoggerMigrationListener;
import com.github.imdmk.spenttime.feature.migration.listener.MigrationListener;
import com.github.imdmk.spenttime.feature.migration.migrator.PlayerMigrator;
import com.github.imdmk.spenttime.feature.migration.provider.PlayerProvider;
import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.shared.Validator;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;
import org.panda_lang.utilities.inject.annotations.PostConstruct;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class AsyncMigrationRunner
        implements MigrationRunner<CompletableFuture<MigrationResult>>, AutoCloseable {

    private static final String EXECUTOR_THREAD_NAME = "spenttime-async-migration-";

    private final PluginLogger logger;
    private final MigrationConfig config;
    private final PlayerProvider provider;
    private final PlayerMigrator migrator;

    private ExecutorService executor;

    @Inject
    public AsyncMigrationRunner(
            @NotNull PluginLogger logger,
            @NotNull MigrationConfig config,
            @NotNull PlayerProvider provider,
            @NotNull PlayerMigrator migrator) {
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.config = Validator.notNull(config, "config cannot be null");
        this.provider = Validator.notNull(provider, "provider cannot be null");
        this.migrator = Validator.notNull(migrator, "migrator cannot be null");
    }

    @PostConstruct
    void postConstruct() {
        this.executor = createNewExecutor(config.migrationMaxConcurrency, config.migrationKeepAliveInterval);
    }

    @Override
    public CompletableFuture<MigrationResult> execute() {
        var runner = new MigrationRunnerImpl(config, provider, migrator, listeners());
        return CompletableFuture.supplyAsync(runner::execute, executor);
    }

    @Override
    public List<MigrationListener> listeners() {
        return List.of(
                new ConfigMigrationListener(config),
                new LoggerMigrationListener(logger)
        );
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private ExecutorService createNewExecutor(int maxConcurrency, Duration keepAlive) {
        return new ThreadPoolExecutor(
                maxConcurrency, maxConcurrency,
                keepAlive.toMillis(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                newThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    private ThreadFactory newThreadFactory() {
        return new ThreadFactory() {

            private final ThreadFactory base = Executors.defaultThreadFactory();
            private final AtomicInteger seq = new AtomicInteger(1);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = base.newThread(r);
                thread.setName(EXECUTOR_THREAD_NAME + seq.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        };
    }
}
