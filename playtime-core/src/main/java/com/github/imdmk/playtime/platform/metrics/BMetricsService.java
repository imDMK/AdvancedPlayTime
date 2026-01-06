package com.github.imdmk.playtime.platform.metrics;

import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.priority.Priority;
import com.github.imdmk.playtime.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service(priority = Priority.LOW)
public class BMetricsService {

    private static final int METRICS_ID = 19362;
    //private final Metrics metrics;

    @Inject
    public BMetricsService(@NotNull Plugin plugin) {
        //this.metrics = new Metrics(plugin, METRICS_ID);
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    void shutdown() {
        //metrics.shutdown();
    }
}
