package com.github.imdmk.playtime.platform.scheduler;

import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.time.Durations;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.time.Duration;

@Service(priority = ComponentPriority.LOWEST)
public final class BukkitTaskScheduler implements TaskScheduler {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    @Inject
    public BukkitTaskScheduler(@NotNull Plugin plugin, @NotNull BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public @NotNull BukkitTask runSync(@NotNull Runnable runnable) {
        return scheduler.runTask(plugin, runnable);
    }

    @Override
    public @NotNull BukkitTask runAsync(@NotNull Runnable runnable) {
        return scheduler.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public @NotNull BukkitTask runLaterAsync(
            @NotNull Runnable runnable,
            @NotNull Duration delay
    ) {
        return scheduler.runTaskLaterAsynchronously(plugin, runnable, Durations.convertToTicks(delay));
    }

    @Override
    public @NotNull BukkitTask runLaterSync(
            @NotNull Runnable runnable,
            @NotNull Duration delay
    ) {
        return scheduler.runTaskLater(plugin, runnable, Durations.convertToTicks(delay));
    }

    @Override
    public @NotNull BukkitTask runTimerSync(
            @NotNull Runnable runnable,
            @NotNull Duration delay,
            @NotNull Duration period
    ) {
        return scheduler.runTaskTimer(plugin, runnable, Durations.convertToTicks(delay), Durations.convertToTicks(period));
    }

    @Override
    public @NotNull BukkitTask runTimerAsync(
            @NotNull Runnable runnable,
            @NotNull Duration delay,
            @NotNull Duration period
    ) {
        return scheduler.runTaskTimerAsynchronously(plugin, runnable, Durations.convertToTicks(delay), Durations.convertToTicks(period));
    }

    @Override
    public void cancelTask(int taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public void cancelAllTasks() {
        scheduler.cancelTasks(plugin);
    }
}
