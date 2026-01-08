package com.github.imdmk.playtime;

import com.github.imdmk.playtime.injector.ComponentManager;
import com.github.imdmk.playtime.injector.processor.ComponentProcessors;
import com.github.imdmk.playtime.injector.subscriber.LocalPublisher;
import com.github.imdmk.playtime.injector.subscriber.Publisher;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeInitializeEvent;
import com.github.imdmk.playtime.injector.subscriber.event.PlayTimeShutdownEvent;
import com.github.imdmk.playtime.platform.logger.BukkitPluginLogger;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.google.common.base.Stopwatch;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;

import java.io.File;
import java.util.concurrent.TimeUnit;

final class PlayTimePlugin {

    private final Publisher publisher;

    PlayTimePlugin(@NotNull Plugin plugin) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final PluginLogger logger = new BukkitPluginLogger(plugin);

        final Injector injector = DependencyInjection.createInjector(resources -> {
            resources.on(Plugin.class).assignInstance(plugin);
            resources.on(Server.class).assignInstance(plugin.getServer());
            resources.on(File.class).assignInstance(plugin.getDataFolder());
            resources.on(PluginLogger.class).assignInstance(logger);
            resources.on(BukkitScheduler.class).assignInstance(plugin.getServer().getScheduler());
        });

        injector.getResources().on(Injector.class).assignInstance(() -> injector);

        this.publisher = new LocalPublisher(injector);

        final ComponentManager componentManager = new ComponentManager(injector, plugin.getClass().getPackageName())
                .addProcessors(ComponentProcessors.defaults(plugin))
                .addPostProcessor(((instance, context) -> this.publisher.subscribe(instance)));

        componentManager.scanAll();
        componentManager.processAll();

        this.publisher.publish(new PlayTimeInitializeEvent());

        //final PlayTimeApi api = injector.newInstance(PlayTimeApiAdapter.class);
        //PlayTimeApiProvider.register(api);

        final long elapsedMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logger.info("Successfully loaded plugin in " + elapsedMillis + "ms!");
    }

    void disable() {
        this.publisher.publish(new PlayTimeShutdownEvent());
        //PlayTimeApiProvider.unregister();
    }
}
