package com.github.imdmk.playtime.core.injector.processor.processors;

import com.github.imdmk.playtime.core.injector.annotations.Task;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;

import java.time.Duration;

public class TaskProcessor implements ComponentProcessor<Task> {

    private final TaskScheduler scheduler;

    public TaskProcessor(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Class<Task> annotation() {
        return Task.class;
    }

    @Override
    public void process(
            Object instance,
            Task annotation,
            ComponentProcessorContext context
    ) {
        Runnable runnable = requireInstance(instance, Runnable.class, Task.class);
        Duration delay = Duration.ofMillis(annotation.delayMillis());
        Duration period = Duration.ofMillis(annotation.periodMillis());
        boolean async = annotation.async();

        if (async) {
            scheduler.runTimerAsync(runnable, delay, period);
        } else {
            scheduler.runTimerSync(runnable, delay, period);
        }
    }
}
