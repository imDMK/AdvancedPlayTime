package com.github.imdmk.playtime;

import com.github.imdmk.playtime.injector.ComponentManager;
import com.github.imdmk.playtime.injector.priority.AnnotationPriorityProvider;
import com.github.imdmk.playtime.platform.logger.BukkitPluginLogger;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;

import java.io.File;

final class PlayTimePlugin {

    //private static final String PREFIX = "AdvancedPlayTime";
    //private static final int PLUGIN_METRICS_ID = 19362;

    private final Injector injector;

    PlayTimePlugin(@NotNull Plugin plugin) {
        injector = DependencyInjection.createInjector(resources -> {
            resources.on(Plugin.class).assignInstance(plugin);
            resources.on(Server.class).assignInstance(plugin.getServer());
            resources.on(File.class).assignInstance(plugin.getDataFolder());
            resources.on(PluginLogger.class).assignInstance(new BukkitPluginLogger(plugin));
        });

        ComponentManager componentManager = new ComponentManager(injector, this.getClass().getPackageName())
                .setPriorityProvider(new AnnotationPriorityProvider())
                .addProcessor();

        componentManager.scanAll();
        componentManager.processAll();

    }

    void disable() {
    }
}
