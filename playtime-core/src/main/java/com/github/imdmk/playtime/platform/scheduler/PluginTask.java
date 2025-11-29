package com.github.imdmk.playtime.platform.scheduler;

import java.time.Duration;

/**
 * Represents a declarative task definition used by the {@link TaskScheduler}.
 *
 * <p>A {@code PluginTask} bundles together:</p>
 * <ul>
 *     <li>the executable logic ({@link #run()}),</li>
 *     <li>a delay before the first execution ({@link #delay()}),</li>
 *     <li>an optional repeating period ({@link #period()}).</li>
 * </ul>
 *
 * <p>Instances are consumed by scheduler methods that accept {@link PluginTask},
 * allowing tasks to be declared as self-contained objects instead of passing
 * raw parameters into every scheduling call.</p>
 *
 * <p><strong>Repeating vs. non-repeating:</strong></p>
 * <ul>
 *     <li>If {@link #period()} returns {@code Duration.ZERO}, the task is executed once after the delay.</li>
 *     <li>If {@link #period()} is greater than zero, the task is executed repeatedly.</li>
 * </ul>
 *
 * <p><strong>Threading:</strong> Whether the task runs synchronously or asynchronously
 * depends solely on the {@link TaskScheduler} method used (e.g., {@code runTimerSync}, {@code runTimerAsync}).</p>
 */
public interface PluginTask extends Runnable {

    /**
     * The task logic to be executed by the scheduler.
     * <p>
     * Called either once (if {@link #period()} is zero) or repeatedly
     * (if {@link #period()} is greater than zero), depending on how
     * the task is scheduled.
     * </p>
     */
    @Override
    void run();

    /**
     * Returns the delay before the first execution.
     *
     * <p>A zero delay means the task should run immediately.</p>
     *
     * @return the initial delay, never {@code null}
     */
    Duration delay();

    /**
     * Returns the repeat period for this task.
     *
     * <p>If this returns {@code Duration.ZERO}, the task is treated as
     * a one-shot task and will not repeat after the first execution.</p>
     *
     * <p>If the value is greater than zero, the scheduler executes the
     * task repeatedly with this interval.</p>
     *
     * @return the repeat interval, never {@code null}
     */
    Duration period();
}
