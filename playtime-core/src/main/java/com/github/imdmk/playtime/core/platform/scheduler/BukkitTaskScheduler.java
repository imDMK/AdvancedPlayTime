package com.github.imdmk.playtime.core.platform.scheduler;

import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.core.time.DurationTicksConverter;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;

@Service(priority = ComponentPriority.LOWEST)
final class BukkitTaskScheduler implements TaskScheduler {

    private final Plugin plugin;
    private final Server server;
    private final BukkitScheduler scheduler;

    @Inject
    BukkitTaskScheduler(Plugin plugin, Server server, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.server = server;
        this.scheduler = scheduler;
    }

    @Override
    public BukkitTask runSync(Runnable runnable) {
        return scheduler.runTask(plugin, runnable);
    }

    @Override
    public BukkitTask runSyncIfNeeded(Runnable runnable) {
        if (server.isPrimaryThread()) {
            runnable.run();
            return null;
        }

        return runSync(runnable);
    }

    @Override
    public BukkitTask runAsync(Runnable runnable) {
        return scheduler.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public BukkitTask runLaterAsync(
            Runnable runnable,
            Duration delay
    ) {
        return scheduler.runTaskLaterAsynchronously(
                plugin,
                runnable,
                DurationTicksConverter.toTicks(delay)
        );
    }

    @Override
    public BukkitTask runLaterSync(
            Runnable runnable,
            Duration delay
    ) {
        return scheduler.runTaskLater(
                plugin,
                runnable,
                DurationTicksConverter.toTicks(delay)
        );
    }

    @Override
    public BukkitTask runTimerSync(
            Runnable runnable,
            Duration delay,
            Duration period
    ) {
        return scheduler.runTaskTimer(
                plugin,
                runnable,
                DurationTicksConverter.toTicks(delay),
                DurationTicksConverter.toTicks(period)
        );
    }

    @Override
    public BukkitTask runTimerAsync(
            Runnable runnable,
            Duration delay,
            Duration period
    ) {
        return scheduler.runTaskTimerAsynchronously(
                plugin,
                runnable,
                DurationTicksConverter.toTicks(delay),
                DurationTicksConverter.toTicks(period)
        );
    }

    @Override
    public void cancelTask(int taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public void cancelAllTasks() {
        scheduler.cancelTasks(plugin);
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        cancelAllTasks();
    }
}
