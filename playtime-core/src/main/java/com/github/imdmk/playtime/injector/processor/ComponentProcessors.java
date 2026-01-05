package com.github.imdmk.playtime.injector.processor;

import com.github.imdmk.playtime.config.ConfigSection;
import com.github.imdmk.playtime.config.ConfigService;
import com.github.imdmk.playtime.database.repository.ormlite.OrmLiteRepository;
import com.github.imdmk.playtime.injector.annotations.ConfigFile;
import com.github.imdmk.playtime.injector.annotations.Controller;
import com.github.imdmk.playtime.injector.annotations.NoneAnnotation;
import com.github.imdmk.playtime.injector.annotations.Repository;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.subscriber.Publisher;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

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
                (service, annotation, context) -> context.injector().getResources().on(service.getClass()).assignInstance(service)
        ));

        processors.add(new FunctionalComponentProcessor<>(
                ConfigFile.class,
                ConfigSection.class,
                (config, annotation, context) -> context.injector().newInstance(ConfigFileProcessor.class).handle(config, annotation, context)
        ));

        processors.add(new FunctionalComponentProcessor<>(
                Repository.class,
                OrmLiteRepository.class,
                (repository, annotation, context) -> repository.start()
        ));

        processors.add(new FunctionalComponentProcessor<>(
                Controller.class,
                Listener.class,
                (listener, annotation, context) -> plugin.getServer().getPluginManager().registerEvents(listener, plugin)
        ));

        processors.add(new FunctionalComponentProcessor<>(
                NoneAnnotation.class,
                Object.class,
                (instance, none, context) -> context.injector().newInstance(NoneAnnotationProcessor.class).handle(instance, none, context)
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
        protected void handle(@NotNull Object instance, @NotNull ConfigFile annotation, @NotNull ComponentProcessorContext context) {
            if (!(instance instanceof ConfigSection configSection)) {
                throw new IllegalArgumentException("Invalid config section: " + instance.getClass().getName());
            }

            configService.create(configSection.getClass());
            context.injector().getResources().on(configSection.getClass()).assignInstance(configSection);
        }

        @Override
        public @NotNull Class<ConfigFile> annotation() {
            return ConfigFile.class;
        }
    }

    private static class NoneAnnotationProcessor extends AbstractComponentProcessor<NoneAnnotation> {

        private final Publisher publisher;

        @Inject
        private NoneAnnotationProcessor(@NotNull Publisher publisher) {
            this.publisher = publisher;
        }

        @Override
        protected void handle(@NotNull Object instance, @NotNull NoneAnnotation annotation, @NotNull ComponentProcessorContext context) {
            publisher.subscribe(instance);
        }

        @Override
        public @NotNull Class<NoneAnnotation> annotation() {
            return NoneAnnotation.class;
        }
    }
}


