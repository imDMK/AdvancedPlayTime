package com.github.imdmk.playtime.core.platform.scheduler;

import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public interface TaskScheduler {

    BukkitTask runSync(Runnable runnable);
    BukkitTask runSyncIfNeeded(Runnable runnable);

    BukkitTask runAsync(Runnable runnable);

    BukkitTask runLaterAsync(Runnable runnable, Duration delay);
    BukkitTask runLaterSync(Runnable runnable, Duration delay);

    BukkitTask runTimerSync(Runnable runnable, Duration delay, Duration period);
    BukkitTask runTimerAsync(Runnable runnable, Duration delay, Duration period);

    void cancelTask(int taskId);
    void cancelAllTasks();
}
