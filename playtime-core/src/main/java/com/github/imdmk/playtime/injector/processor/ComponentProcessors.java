package com.github.imdmk.playtime.injector.processor;

import com.github.imdmk.playtime.config.ConfigSection;
import com.github.imdmk.playtime.config.ConfigService;
import com.github.imdmk.playtime.database.DatabaseBootstrap;
import com.github.imdmk.playtime.database.repository.ormlite.OrmLiteRepository;
import com.github.imdmk.playtime.injector.annotations.ConfigFile;
import com.github.imdmk.playtime.injector.annotations.Controller;
import com.github.imdmk.playtime.injector.annotations.Database;
import com.github.imdmk.playtime.injector.annotations.Gui;
import com.github.imdmk.playtime.injector.annotations.Repository;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.annotations.lite.LiteArgument;
import com.github.imdmk.playtime.injector.annotations.lite.LiteHandler;
import com.github.imdmk.playtime.injector.annotations.placeholderapi.Placeholder;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Resources;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.List;

public final class ComponentProcessors {

    private ComponentProcessors() {
        throw new UnsupportedOperationException("This is utility class and cannot be instantiated.");
    }

    public static List<ProcessorContainer<?>> defaults(@NotNull Plugin plugin) {
        return List.of(
                ProcessorBuilder.forAnnotation(Service.class)
                        .handle((instance, annotation, ctx) -> {
                            final Resources resources = ctx.injector().getResources();

                            // bind
                            resources.on(instance.getClass())
                                    .assignInstance(instance);

                            // bind interfaces
                            for (final Class<?> interfaces : instance.getClass().getInterfaces()) {
                                resources.on(interfaces).assignInstance(instance);
                            }
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(ConfigFile.class)
                        .handle((instance, annotation, ctx) -> ctx.injector().newInstance(ConfigFileProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(Database.class)
                        .handle((instance, annotation, ctx) -> {
                            if (!(instance instanceof DatabaseBootstrap databaseBootstrap)) {
                                throw new IllegalStateException("@Database can only be used on with DatabaseBootstrap class");
                            }

                            databaseBootstrap.start();

                            ctx.injector().getResources()
                                    .on(DatabaseBootstrap.class)
                                    .assignInstance(databaseBootstrap);
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(Repository.class)
                        .handle((instance, annotation, ctx) -> {
                            if (!(instance instanceof OrmLiteRepository<?,?> repository)) {
                                return;
                            }

                            repository.start();

                            ctx.injector().getResources()
                                    .on(repository.getClass())
                                    .assignInstance(repository);
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(Controller.class)
                        .handle((instance, annotation, ctx) -> {
                            if (!(instance instanceof Listener listener)) {
                                throw new IllegalStateException(
                                        "@Controller must implement Listener: "
                                                + instance.getClass().getName()
                                );
                            }

                            plugin.getServer()
                                    .getPluginManager()
                                    .registerEvents(listener, plugin);
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(Gui.class)
                        .handle()
                        .build(),

                ProcessorBuilder.forAnnotation(Placeholder.class)
                        .handle()
                        .build(),

                ProcessorBuilder.forAnnotation(Command.class)
                        .handle((instance, annotation, ctx) -> ctx.injector().newInstance(LiteCommandProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(LiteHandler.class)
                        .handle((instance, annotation, ctx) -> ctx.injector().newInstance(LiteHandlerProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(LiteArgument.class)
                        .handle((instance, annotation, ctx) -> ctx.injector().newInstance(LiteArgumentProcessor.class).process(instance, annotation, ctx))
                        .build()

        );
    }

    @Inject
    private record ConfigFileProcessor(@NotNull ConfigService configService)
                implements ComponentProcessor<ConfigFile> {

            @Override
            public void process(
                    @NotNull Object instance,
                    @NotNull ConfigFile annotation,
                    @NotNull ComponentProcessorContext context
            ) {
                if (!(instance instanceof ConfigSection configSection)) {
                    throw new IllegalStateException(
                            "@ConfigFile can only be used on ConfigSection: "
                                    + instance.getClass().getName()
                    );
                }

                configService.create(configSection.getClass());
                context.injector().getResources()
                        .on(instance.getClass())
                        .assignInstance(instance);
            }

            @Override
            @NotNull
            public Class<ConfigFile> annotation() {
                return ConfigFile.class;
            }
    }

    private record

    @Inject
    private record LiteHandlerProcessor(@NotNull LiteCommandsBuilder<?, ?, ?> liteBuilder)
            implements ComponentProcessor<LiteHandler> {

        @Override
        public @NotNull Class<LiteHandler> annotation() {
            return LiteHandler.class;
        }

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void process(
                @NotNull Object instance,
                @NotNull LiteHandler annotation,
                @NotNull ComponentProcessorContext context
        ) {
            if (!(instance instanceof ResultHandler handler)) {
                throw new IllegalStateException(
                        "@LiteHandler can only be used on ResultHandler: "
                                + instance.getClass().getName()
                );
            }

            liteBuilder.result(annotation.value(), handler);
        }
    }

    @Inject
    private record LiteCommandProcessor(@NotNull LiteCommandsBuilder<?, ?, ?> liteBuilder)
            implements ComponentProcessor<Command> {

        @Override
        public @NotNull Class<Command> annotation() {
            return Command.class;
        }

        @Override
        public void process(
                @NotNull Object instance,
                @NotNull Command annotation,
                @NotNull ComponentProcessorContext context
        ) {
            liteBuilder.commands(instance);
        }
    }

    @Inject
    private record LiteArgumentProcessor(@NotNull LiteCommandsBuilder<?, ?, ?> liteBuilder)
            implements ComponentProcessor<LiteArgument> {

        @Override
        public @NotNull Class<LiteArgument> annotation() {
            return LiteArgument.class;
        }

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void process(
                @NotNull Object instance,
                @NotNull LiteArgument annotation,
                @NotNull ComponentProcessorContext context
        ) {
            if (!(instance instanceof ArgumentResolver argumentResolver)) {
                throw new IllegalStateException(
                        "@LiteArgument can only be used on ArgumentResolver: "
                                + instance.getClass().getName()
                );
            }

            liteBuilder.argument(annotation.type(), ArgumentKey.of(annotation.name()), argumentResolver);
        }
    }

}
