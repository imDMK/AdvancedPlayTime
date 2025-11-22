package com.github.imdmk.spenttime.feature.migration.runner;

import com.github.imdmk.spenttime.feature.migration.MigrationConfig;
import com.github.imdmk.spenttime.feature.migration.MigrationResult;
import com.github.imdmk.spenttime.feature.migration.listener.MigrationListener;
import com.github.imdmk.spenttime.feature.migration.migrator.PlayerMigrator;
import com.github.imdmk.spenttime.feature.migration.provider.PlayerProvider;
import com.github.imdmk.spenttime.shared.Validator;
import com.google.common.base.Stopwatch;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

final class MigrationRunnerImpl {

    private final MigrationConfig config;
    private final PlayerProvider provider;
    private final PlayerMigrator migrator;
    private final List<MigrationListener> listeners;

    MigrationRunnerImpl(
            @NotNull MigrationConfig config,
            @NotNull PlayerProvider provider,
            @NotNull PlayerMigrator migrator,
            @NotNull List<MigrationListener> listeners) {
        this.config = Validator.notNull(config, "config cannot be null");
        this.provider = Validator.notNull(provider, "provider cannot be null");
        this.migrator = Validator.notNull(migrator, "migrator cannot be null");
        this.listeners = Validator.notNull(listeners, "listeners cannot be null");
    }

    MigrationResult execute() {
        if (!config.initialServerMigrationEnabled) {
            return MigrationResult.empty();
        }

        final Stopwatch stopwatch = Stopwatch.createStarted();

        final Collection<OfflinePlayer> players = provider.getAllPlayers();
        final int total = players.size();

        listenersForEach(l -> l.onStart(total));
        if (total == 0) {
            final MigrationResult empty = MigrationResult.empty();
            listenersForEach(l -> l.onEnd(empty));
            return empty;
        }

        final AtomicInteger success = new AtomicInteger();
        final AtomicInteger failed  = new AtomicInteger();
        final AtomicInteger inflight = new AtomicInteger(total);

        final Semaphore limiter = new Semaphore(config.migrationMaxConcurrency);
        final CompletableFuture<Void> allDone = new CompletableFuture<>();

        for (final OfflinePlayer player : players) {
            limiter.acquireUninterruptibly();

            migrator.migrate(player)
                    .orTimeout(config.migrationTaskTimeout.toMillis(), TimeUnit.MILLISECONDS)
                    .whenComplete((u, e) -> {
                        try {
                            if (e == null) {
                                success.incrementAndGet();
                                listenersForEach(l -> l.onSuccess(player));
                            } else {
                                failed.incrementAndGet();
                                listenersForEach(l -> l.onFailed(player, e));
                            }
                        } finally {
                            limiter.release();
                            if (inflight.decrementAndGet() == 0) {
                                allDone.complete(null);
                            }
                        }
                    });
        }

        allDone.orTimeout(config.migrationGlobalTimeout.toMillis(), TimeUnit.MILLISECONDS).join();

        final Duration took = stopwatch.stop().elapsed();
        final MigrationResult result = new MigrationResult(total, success.get(), failed.get(), took);

        listenersForEach(l -> l.onEnd(result));

        return result;
    }

    private void listenersForEach(@NotNull Consumer<MigrationListener> listenerConsumer) {
        for (final var listener : listeners) {
            listenerConsumer.accept(listener);
        }
    }

}
