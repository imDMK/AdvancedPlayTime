package com.github.imdmk.playtime.platform.scheduler;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Abstraction layer over the Bukkit {@link org.bukkit.scheduler.BukkitScheduler},
 * providing a clean, consistent API for scheduling synchronous and asynchronous tasks
 * using either raw {@link Runnable} instances or declarative {@link PluginTask} objects.
 *
 * <p><strong>Threading rules:</strong></p>
 * <ul>
 *     <li><b>Sync methods</b> execute on the main server thread.</li>
 *     <li><b>Async methods</b> execute off the main thread and must not access Bukkit API objects that require sync.</li>
 * </ul>
 *
 * <p><strong>Delay & period units:</strong> All {@code Duration} values are converted to
 * Minecraft ticks (1 tick = 50ms).</p>
 *
 * <p><strong>PluginTask usage:</strong> All overloads accepting {@link PluginTask}
 * automatically use the task's declared delay and period.</p>
 */
public interface TaskScheduler {

    /**
     * Executes the given runnable immediately on the main server thread.
     *
     * @param runnable non-null logic to execute
     * @return the task handle
     */
    BukkitTask runSync(@NotNull Runnable runnable);

    /**
     * Executes the given {@link PluginTask} immediately on the main server thread.
     *
     * <p>{@link PluginTask#delay()} and {@link PluginTask#period()} are ignored;
     * this method always runs instantly.</p>
     *
     * @param task non-null task instance
     * @return the task handle
     */
    BukkitTask runSync(@NotNull PluginTask task);

    /**
     * Executes the given runnable immediately on a separate thread.
     *
     * @param runnable non-null logic to execute asynchronously
     * @return the task handle
     */
    BukkitTask runAsync(@NotNull Runnable runnable);

    /**
     * Executes the given {@link PluginTask} immediately on a separate thread.
     *
     * <p>{@link PluginTask#delay()} and {@link PluginTask#period()} are ignored;
     * this method always runs instantly.</p>
     *
     * @param task non-null task instance
     * @return the task handle
     */
    BukkitTask runAsync(@NotNull PluginTask task);

    /**
     * Executes the runnable asynchronously after the given delay.
     *
     * @param runnable task logic
     * @param delay    delay before execution (converted to ticks)
     * @return the task handle
     */
    BukkitTask runLaterAsync(@NotNull Runnable runnable, @NotNull Duration delay);

    /**
     * Executes the {@link PluginTask} asynchronously after {@link PluginTask#delay()}.
     *
     * <p>Runs once unless {@link PluginTask#period()} is non-zero.</p>
     *
     * @param task task definition
     * @return the task handle
     */
    BukkitTask runLaterAsync(@NotNull PluginTask task);

    /**
     * Executes the runnable synchronously after the given delay.
     *
     * @param runnable task logic
     * @param delay    delay before execution (converted to ticks)
     * @return the task handle
     */
    BukkitTask runLaterSync(@NotNull Runnable runnable, @NotNull Duration delay);

    /**
     * Executes the {@link PluginTask} synchronously after {@link PluginTask#delay()}.
     *
     * <p>Runs once unless {@link PluginTask#period()} is non-zero.</p>
     *
     * @param task task definition
     * @return the task handle
     */
    BukkitTask runLaterSync(@NotNull PluginTask task);

    /**
     * Schedules a synchronous repeating task.
     *
     * @param runnable logic to execute
     * @param delay    initial delay before the first run
     * @param period   time between runs
     * @return the created repeating task
     */
    BukkitTask runTimerSync(@NotNull Runnable runnable, @NotNull Duration delay, @NotNull Duration period);

    /**
     * Schedules a synchronous repeating {@link PluginTask} using its delay/period.
     *
     * @param task task definition
     * @return the created repeating task
     */
    BukkitTask runTimerSync(@NotNull PluginTask task);

    /**
     * Schedules an asynchronous repeating task.
     *
     * @param runnable logic to execute
     * @param delay    initial delay before the first execution
     * @param period   time between consecutive executions
     * @return the created repeating task
     */
    BukkitTask runTimerAsync(@NotNull Runnable runnable, @NotNull Duration delay, @NotNull Duration period);

    /**
     * Schedules an asynchronous repeating {@link PluginTask} using its delay/period.
     *
     * @param task task definition
     * @return the created repeating task
     */
    BukkitTask runTimerAsync(@NotNull PluginTask task);

    /**
     * Cancels a scheduled task via its Bukkit ID.
     *
     * @param taskId scheduler task ID
     */
    void cancelTask(int taskId);

    /**
     * Cancels all tasks created for the associated plugin.
     *
     * <p>Called during plugin shutdown.</p>
     */
    void shutdown();
}
