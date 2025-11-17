package com.github.imdmk.spenttime.platform.scheduler;

import com.github.imdmk.spenttime.shared.Validator;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Concrete {@link TaskScheduler} implementation backed by the Bukkit {@link BukkitScheduler}.
 * <p>
 * Provides convenient methods for executing synchronous and asynchronous tasks,
 * as well as delayed and repeating operations bound to a specific {@link Plugin}.
 * <p>
 * All delay and period values are expressed in <strong>ticks</strong> (20 ticks = 1 second).
 * <p>
 * <strong>Thread safety:</strong> This class is thread-safe and stateless.
 * <p>
 * <strong>Lifecycle:</strong> The {@link #shutdown()} method cancels all tasks
 * currently scheduled under the associated plugin instance.
 */
public final class BukkitTaskScheduler implements TaskScheduler {

    private final Plugin plugin;
    private final BukkitScheduler bukkitScheduler;

    public BukkitTaskScheduler(@NotNull Plugin plugin, @NotNull BukkitScheduler bukkitScheduler) {
        this.plugin = Validator.notNull(plugin, "plugin cannot be null");
        this.bukkitScheduler = Validator.notNull(bukkitScheduler, "bukkitScheduler cannot be null");
    }

    /**
     * Executes a runnable synchronously on the main server thread.
     *
     * @param runnable the task to execute; must not be {@code null}
     * @return the created {@link BukkitTask} handle
     * @throws NullPointerException if {@code runnable} is {@code null}
     */
    @Override
    public @NotNull BukkitTask runSync(@NotNull Runnable runnable) {
        Validator.notNull(runnable, "runnable cannot be null");
        return bukkitScheduler.runTask(plugin, runnable);
    }

    /**
     * Executes a runnable asynchronously in a separate thread.
     *
     * @param runnable the task to execute; must not be {@code null}
     * @return the created {@link BukkitTask} handle
     * @throws NullPointerException if {@code runnable} is {@code null}
     */
    @Override
    public @NotNull BukkitTask runAsync(@NotNull Runnable runnable) {
        Validator.notNull(runnable, "runnable cannot be null");
        return bukkitScheduler.runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Executes a runnable asynchronously after a specified delay.
     *
     * @param runnable the task to execute; must not be {@code null}
     * @param delay    delay in ticks before execution (20 ticks = 1 second)
     * @return the created {@link BukkitTask} handle
     * @throws NullPointerException if {@code runnable} is {@code null}
     */
    @Override
    public @NotNull BukkitTask runLaterAsync(@NotNull Runnable runnable, long delay) {
        Validator.notNull(runnable, "runnable cannot be null");
        return bukkitScheduler.runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    /**
     * Executes a runnable synchronously after a specified delay.
     *
     * @param runnable the task to execute; must not be {@code null}
     * @param delay    delay in ticks before execution (20 ticks = 1 second)
     * @return the created {@link BukkitTask} handle
     * @throws NullPointerException if {@code runnable} is {@code null}
     */
    @Override
    public BukkitTask runLaterSync(@NotNull Runnable runnable, long delay) {
        Validator.notNull(runnable, "runnable cannot be null");
        return bukkitScheduler.runTaskLater(plugin, runnable, delay);
    }

    /**
     * Executes a repeating runnable synchronously on the main thread.
     *
     * @param runnable the task to execute; must not be {@code null}
     * @param delay    initial delay in ticks before the first run
     * @param period   interval in ticks between consecutive executions
     * @return the created {@link BukkitTask} handle
     * @throws NullPointerException if {@code runnable} is {@code null}
     */
    @Override
    public @NotNull BukkitTask runTimerSync(@NotNull Runnable runnable, long delay, long period) {
        Validator.notNull(runnable, "runnable cannot be null");
        return bukkitScheduler.runTaskTimer(plugin, runnable, delay, period);
    }

    /**
     * Executes a repeating runnable asynchronously.
     *
     * @param runnable the task to execute; must not be {@code null}
     * @param delay    initial delay in ticks before the first run
     * @param period   interval in ticks between consecutive executions
     * @return the created {@link BukkitTask} handle
     * @throws NullPointerException if {@code runnable} is {@code null}
     */
    @Override
    public @NotNull BukkitTask runTimerAsync(@NotNull Runnable runnable, long delay, long period) {
        Validator.notNull(runnable, "runnable cannot be null");
        return bukkitScheduler.runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    /**
     * Cancels the task associated with the given Bukkit scheduler ID.
     *
     * @param taskId the numeric task identifier
     */
    @Override
    public void cancelTask(int taskId) {
        bukkitScheduler.cancelTask(taskId);
    }

    /**
     * Cancels all scheduled tasks that were registered by this plugin instance.
     * <p>
     * Useful when unloading or disabling the plugin to ensure no residual
     * scheduled tasks continue to run.
     */
    @Override
    public void shutdown() {
        bukkitScheduler.cancelTasks(plugin);
    }
}
