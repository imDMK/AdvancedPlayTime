package com.github.imdmk.playtime.injector.processor;

import com.github.imdmk.playtime.config.ConfigSection;
import com.github.imdmk.playtime.config.ConfigService;
import com.github.imdmk.playtime.database.DatabaseBootstrap;
import com.github.imdmk.playtime.database.repository.ormlite.OrmLiteRepository;
import com.github.imdmk.playtime.injector.annotations.ConfigFile;
import com.github.imdmk.playtime.injector.annotations.Controller;
import com.github.imdmk.playtime.injector.annotations.Database;
import com.github.imdmk.playtime.injector.annotations.Repository;
import com.github.imdmk.playtime.injector.annotations.Service;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Resources;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.lang.module.ResolutionException;
import java.util.ArrayList;
import java.util.List;

public final class ComponentProcessors {

    /**
     * Initializes all default component processors.
     * <p>
     * This method is called once during plugin startup.
     * Processors may perform eager initialization and side effects.
     */
    public static List<ComponentProcessor<?>> defaults(@NotNull Plugin plugin) {
        final List<ComponentProcessor<?>> processors = new ArrayList<>();

        processors.add(new FunctionalComponentProcessor<>(
                Service.class,
                Object.class,
                (service, annotation, context) -> {
                    final Resources resources = context.injector().getResources();

                    resources.on(service.getClass()).assignInstance(() -> service);

                    for (final Class<?> interfaces : service.getClass().getInterfaces()) {
                        resources.on(interfaces).assignInstance(service);
                    }
                }
        ));

        processors.add(new FunctionalComponentProcessor<>(
                ConfigFile.class,
                ConfigSection.class,
                (config, annotation, context) -> context.injector().newInstance(ConfigFileProcessor.class).process(config, annotation, context)
        ));

        processors.add(new FunctionalComponentProcessor<>(
                Database.class,
                DatabaseBootstrap.class,
                (database, annotation, context) -> {
                    database.start();
                    context.injector().getResources().on(database.getClass()).assignInstance(database);
                }
        ));

        processors.add(new FunctionalComponentProcessor<>(
                Repository.class,
                OrmLiteRepository.class,
                (repository, annotation, context) -> {
                    repository.start();
                    context.injector().getResources().on(repository.getClass()).assignInstance(repository);
                }
        ));

        processors.add(new FunctionalComponentProcessor<>(
                Controller.class,
                Listener.class,
                (listener, annotation, context) -> plugin.getServer().getPluginManager().registerEvents(listener, plugin)
        ));

        return processors;
    }

    private static class ConfigFileProcessor extends AbstractComponentProcessor<ConfigFile> {

        private final ConfigService configService;

        @Inject
        private ConfigFileProcessor(@NotNull ConfigService configService) {
            this.configService = configService;
        }

        @Override
        public void process(@NotNull Object instance, @NotNull ConfigFile annotation, @NotNull ComponentProcessorContext context) {
            if (!(instance instanceof ConfigSection configSection)) {
                throw new IllegalArgumentException("Invalid config section: " + instance.getClass().getName());
            }

            configService.create(configSection.getClass());
            context.injector().getResources().on(configSection.getClass()).assignInstance(configSection);
        }

        @Override
        @NotNull
        public Class<ConfigFile> annotation() {
            return ConfigFile.class;
        }
    }
}


