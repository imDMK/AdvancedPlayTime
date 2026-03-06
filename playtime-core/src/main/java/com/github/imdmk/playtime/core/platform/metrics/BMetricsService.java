package com.github.imdmk.playtime.core.platform.metrics;

import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.injector.subscriber.Subscribe;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeShutdownEvent;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service
final class BMetricsService {

    private static final int METRICS_ID = 19362;
    private final Metrics metrics;

    @Inject
    BMetricsService(Plugin plugin) {
        this.metrics = new Metrics(plugin, METRICS_ID);
    }

    @Subscribe(event = PlayTimeShutdownEvent.class)
    private void shutdown() {
        metrics.shutdown();
    }
}
