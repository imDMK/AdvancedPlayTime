package com.github.imdmk.playtime.core;

import com.github.imdmk.playtime.api.PlayTimeApi;
import com.github.imdmk.playtime.api.PlayTimeApiProvider;
import com.github.imdmk.playtime.core.feature.playtime.PlayTimeApiAdapter;
import com.github.imdmk.playtime.core.injector.ComponentManager;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessors;
import com.github.imdmk.playtime.core.injector.subscriber.LocalPublisher;
import com.github.imdmk.playtime.core.injector.subscriber.Publisher;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeInitializeEvent;
import com.github.imdmk.playtime.core.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.core.platform.logger.BukkitPluginLogger;
import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import com.google.common.base.Stopwatch;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;

import java.io.File;
import java.util.concurrent.TimeUnit;

final class PlayTimeCore {

    private static final String BASE_PACKAGE = "com.github.imdmk.playtime";

    private final Publisher publisher;

    PlayTimeCore(Plugin plugin) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        PluginLogger logger = new BukkitPluginLogger(plugin);

        Injector injector = DependencyInjection.createInjector(resources -> {
            resources.on(Plugin.class).assignInstance(plugin);
            resources.on(Server.class).assignInstance(plugin.getServer());
            resources.on(File.class).assignInstance(plugin.getDataFolder());
            resources.on(PluginLogger.class).assignInstance(logger);
            resources.on(BukkitScheduler.class).assignInstance(plugin.getServer().getScheduler());
        });

        injector.getResources().on(Injector.class).assignInstance(() -> injector);

        this.publisher = new LocalPublisher(injector);

        ComponentManager componentManager = new ComponentManager(injector, BASE_PACKAGE)
                .addProcessors(ComponentProcessors.defaults(plugin))
                .addPostProcessor((instance, context) -> this.publisher.subscribe(instance));

        componentManager.scanAll();
        componentManager.processAll();

        PlayTimeApi api = injector.newInstance(PlayTimeApiAdapter.class);
        PlayTimeApiProvider.register(api);

        this.publisher.publish(new PlayTimeInitializeEvent());

        long elapsedMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("Successfully loaded plugin in " + elapsedMillis + "ms!");
    }

    void disable() {
        this.publisher.publish(new PlayTimeShutdownEvent());
        PlayTimeApiProvider.unregister();
    }
}
