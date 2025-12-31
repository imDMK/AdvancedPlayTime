package com.github.imdmk.playtime.platform.scheduler;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public interface TaskScheduler {

    BukkitTask runSync(@NotNull Runnable runnable);

    BukkitTask runAsync(@NotNull Runnable runnable);

    BukkitTask runLaterAsync(@NotNull Runnable runnable, @NotNull Duration delay);

    BukkitTask runLaterSync(@NotNull Runnable runnable, @NotNull Duration delay);

    BukkitTask runTimerSync(@NotNull Runnable runnable, @NotNull Duration delay, @NotNull Duration period);

    BukkitTask runTimerAsync(@NotNull Runnable runnable, @NotNull Duration delay, @NotNull Duration period);

    void cancelTask(int taskId);

    void cancelAllTasks();
}
