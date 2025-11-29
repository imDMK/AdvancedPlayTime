package com.github.imdmk.playtime.infrastructure.module.phase;

import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;

/**
 * Functional phase interface responsible for scheduling asynchronous or repeating tasks.
 * <p>
 * Implementations register all background or periodic tasks needed by a module
 * through the provided {@link TaskScheduler}.
 */
@FunctionalInterface
public interface TaskPhase {

    /**
     * Registers all scheduled tasks for this module.
     *
     * @param scheduler the task scheduler used to register Bukkit or async tasks (never {@code null})
     */
    void schedule(@NotNull TaskScheduler scheduler);
}
