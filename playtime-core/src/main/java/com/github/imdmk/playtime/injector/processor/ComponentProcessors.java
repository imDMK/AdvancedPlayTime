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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ComponentProcessors {

    @Inject
    public static List<ComponentProcessor<?>> defaults(
            @NotNull Plugin plugin,
            @NotNull ConfigService configService,
            @NotNull Publisher publisher
    ) {
        List<ComponentProcessor<?>> processors = new ArrayList<>();

        processors.add(new FunctionalComponentProcessor<>(
                ConfigFile.class,
                ConfigSection.class,
                (config, annotation, context) -> configService.create(config.getClass())
        ));

        processors.add(new FunctionalComponentProcessor<>(
                Service.class,
                Object.class,
                (instance, annotation, context) -> {}
        ));

        processors.add(new FunctionalComponentProcessor<>(
                Repository.class,
                OrmLiteRepository.class,
                (repository, annotation, context) -> {
                    try {
                        repository.start();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        ));

        processors.add(new FunctionalComponentProcessor<>(
                Controller.class,
                Listener.class,
                (listener, annotation, context) -> plugin.getServer().getPluginManager().registerEvents(listener, plugin)
        ));

        processors.add(new FunctionalComponentProcessor<>(
                NoneAnnotation.class,
                Object.class,
                (instance, none, context) -> publisher.subscribe(instance)
        ));

        return processors;
    }
}


