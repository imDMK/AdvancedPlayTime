package com.github.imdmk.playtime.platform.scheduler;

import com.github.imdmk.playtime.shared.validate.Validator;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * {@link TaskScheduler} implementation backed by the Bukkit {@link BukkitScheduler}.
 *
 * <p>Provides a clean, Duration-based API for scheduling synchronous and asynchronous
 * tasks, including delayed and repeating executions.</p>
 *
 * <p>All time values are expressed using {@link Duration} and internally converted
 * to Minecraft ticks (1 tick = 50 ms).</p>
 *
 * <p><strong>Thread-safety:</strong> This class is thread-safe. It holds only immutable
 * references to {@link Plugin} and {@link BukkitScheduler}.</p>
 */
public final class BukkitTaskScheduler implements TaskScheduler {

    /** Number of milliseconds per Minecraft tick. */
    private static final long MILLIS_PER_TICK = 50L;

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public BukkitTaskScheduler(@NotNull Plugin plugin, @NotNull BukkitScheduler scheduler) {
        this.plugin = Validator.notNull(plugin, "plugin cannot be null");
        this.scheduler = Validator.notNull(scheduler, "scheduler cannot be null");
    }

    @Override
    public @NotNull BukkitTask runSync(@NotNull Runnable runnable) {
        Validator.notNull(runnable, "runnable cannot be null");
        return scheduler.runTask(plugin, runnable);
    }

    @Override
    public @NotNull BukkitTask runSync(@NotNull PluginTask task) {
        Validator.notNull(task, "task cannot be null");
        return scheduler.runTask(plugin, task);
    }

    @Override
    public @NotNull BukkitTask runAsync(@NotNull Runnable runnable) {
        Validator.notNull(runnable, "runnable cannot be null");
        return scheduler.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public @NotNull BukkitTask runAsync(@NotNull PluginTask task) {
        Validator.notNull(task, "task cannot be null");
        return scheduler.runTaskAsynchronously(plugin, task);
    }

    @Override
    public @NotNull BukkitTask runLaterAsync(@NotNull Runnable runnable, @NotNull Duration delay) {
        Validator.notNull(runnable, "runnable cannot be null");
        Validator.notNull(delay, "delay cannot be null");
        return scheduler.runTaskLaterAsynchronously(plugin, runnable, toTicks(delay));
    }

    @Override
    public @NotNull BukkitTask runLaterAsync(@NotNull PluginTask task) {
        Validator.notNull(task, "task cannot be null");
        return runLaterAsync(task, task.delay());
    }

    @Override
    public @NotNull BukkitTask runLaterSync(@NotNull Runnable runnable, @NotNull Duration delay) {
        Validator.notNull(runnable, "runnable cannot be null");
        Validator.notNull(delay, "delay cannot be null");
        return scheduler.runTaskLater(plugin, runnable, toTicks(delay));
    }

    @Override
    public @NotNull BukkitTask runLaterSync(@NotNull PluginTask task) {
        Validator.notNull(task, "task cannot be null");
        return runLaterSync(task, task.delay());
    }

    @Override
    public @NotNull BukkitTask runTimerSync(
            @NotNull Runnable runnable,
            @NotNull Duration delay,
            @NotNull Duration period
    ) {
        Validator.notNull(runnable, "runnable cannot be null");
        Validator.notNull(delay, "delay cannot be null");
        Validator.notNull(period, "period cannot be null");

        return scheduler.runTaskTimer(plugin, runnable, toTicks(delay), toTicks(period));
    }

    @Override
    public @NotNull BukkitTask runTimerSync(@NotNull PluginTask task) {
        Validator.notNull(task, "task cannot be null");
        return runTimerSync(task, task.delay(), task.period());
    }

    @Override
    public @NotNull BukkitTask runTimerAsync(
            @NotNull Runnable runnable,
            @NotNull Duration delay,
            @NotNull Duration period
    ) {
        Validator.notNull(runnable, "runnable cannot be null");
        Validator.notNull(delay, "delay cannot be null");
        Validator.notNull(period, "period cannot be null");

        return scheduler.runTaskTimerAsynchronously(plugin, runnable, toTicks(delay), toTicks(period));
    }

    @Override
    public @NotNull BukkitTask runTimerAsync(@NotNull PluginTask task) {
        Validator.notNull(task, "task cannot be null");
        return runTimerAsync(task, task.delay(), task.period());
    }

    @Override
    public void cancelTask(int taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public void shutdown() {
        scheduler.cancelTasks(plugin);
    }

    /**
     * Converts the given duration to Minecraft ticks.
     * <p>
     * Fractions are truncated. Negative durations return {@code 0}.
     *
     * @param duration duration to convert; must not be null
     * @return number of ticks (≥ 0)
     */
    private static int toTicks(@NotNull Duration duration) {
        Validator.notNull(duration, "duration cannot be null");

        long ticks = duration.toMillis() / MILLIS_PER_TICK;
        return ticks <= 0 ? 0 : (int) ticks;
    }
}
