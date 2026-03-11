package com.github.imdmk.playtime.core.injector.processor;

import com.github.imdmk.playtime.core.config.ConfigSection;
import com.github.imdmk.playtime.core.config.ConfigService;
import com.github.imdmk.playtime.core.database.DatabaseBootstrap;
import com.github.imdmk.playtime.core.database.repository.ormlite.OrmLiteRepository;
import com.github.imdmk.playtime.core.injector.annotations.*;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteArgument;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteContext;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteHandler;
import com.github.imdmk.playtime.core.injector.annotations.placeholderapi.Placeholder;
import com.github.imdmk.playtime.core.platform.gui.GuiRegistry;
import com.github.imdmk.playtime.core.platform.gui.IdentifiableGui;
import com.github.imdmk.playtime.core.platform.litecommands.LiteCommandsConfigurer;
import com.github.imdmk.playtime.core.platform.placeholder.PlaceholderService;
import com.github.imdmk.playtime.core.platform.placeholder.PluginPlaceholder;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.panda_lang.utilities.inject.Resources;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.lang.reflect.Field;
import java.util.List;

public final class ComponentProcessors {

    private ComponentProcessors() {
        throw new UnsupportedOperationException("This is utility class and cannot be instantiated.");
    }

    public static List<ProcessorContainer<?>> defaults(Plugin plugin) {
        return List.of(
                ProcessorBuilder.forAnnotation(Service.class)
                        .handle((instance, annotation, ctx) -> {
                            Resources resources = ctx.injector().getResources();

                            // bind
                            resources.on(instance.getClass())
                                    .assignInstance(instance);

                            // bind interfaces
                            for (Class<?> interfaces : instance.getClass().getInterfaces()) {
                                resources.on(interfaces).assignInstance(instance);
                            }
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(ConfigFile.class)
                        .handle((instance, annotation, ctx) -> ctx.injector().newInstance(ConfigFileProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(Database.class)
                        .handle((instance, annotation, ctx) -> {
                            DatabaseBootstrap databaseBootstrap = requireInstance(instance, DatabaseBootstrap.class, Database.class);

                            databaseBootstrap.start();
                            ctx.injector().getResources()
                                    .on(DatabaseBootstrap.class)
                                    .assignInstance(databaseBootstrap);
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(Repository.class)
                        .handle((instance, annotation, ctx) -> {
                            OrmLiteRepository<?, ?> repository = requireInstance(instance, OrmLiteRepository.class, OrmLiteRepository.class);

                            repository.start();
                            ctx.injector().getResources()
                                    .on(repository.getClass())
                                    .assignInstance(repository);
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(Controller.class)
                        .handle((instance, annotation, ctx) -> {
                            Listener listener = requireInstance(instance, Listener.class, Controller.class);
                            plugin.getServer()
                                    .getPluginManager()
                                    .registerEvents(listener, plugin);
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(Gui.class)
                        .handle((instance, annotation, ctx) ->
                                ctx.injector().newInstance(GuiProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(Placeholder.class)
                        .handle((instance, annotation, ctx) ->
                                ctx.injector().newInstance(PlaceholderProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(LiteCommand.class)
                        .handle((instance, annotation, ctx) ->
                                ctx.injector().newInstance(LiteCommandProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(LiteHandler.class)
                        .handle((instance, annotation, ctx) ->
                                ctx.injector().newInstance(LiteHandlerProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(LiteArgument.class)
                        .handle((instance, annotation, ctx) ->
                                ctx.injector().newInstance(LiteArgumentProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(LiteContext.class)
                        .handle((instance, annotation, ctx) ->
                                ctx.injector().newInstance(LiteContextProcessor.class).process(instance, annotation, ctx))
                        .build()
        );
    }

    static <T> T requireInstance(
            Object instance,
            Class<T> expectedType,
            Class<?> annotation
    ) {
        if (!expectedType.isInstance(instance)) {
            throw new IllegalStateException(
                    "@" + annotation.getSimpleName()
                            + " can only be used on "
                            + expectedType.getSimpleName()
                            + ": " + instance.getClass().getName()
            );
        }

        return expectedType.cast(instance);
    }

    @Inject
    private record GuiProcessor(GuiRegistry guiRegistry)
            implements ComponentProcessor<Gui> {

        @Override
        public void process(Object instance, Gui annotation, ComponentProcessorContext context) {
            IdentifiableGui identifiableGui = requireInstance(instance, IdentifiableGui.class, Gui.class);
            guiRegistry.register(identifiableGui);
        }


        @Override
        public Class<Gui> annotation() {
            return Gui.class;
        }
    }

    @Inject
    private record ConfigFileProcessor(ConfigService configService)
            implements ComponentProcessor<ConfigFile> {

            @Override
            public void process(
                    Object instance,
                    ConfigFile annotation,
                    ComponentProcessorContext context
            ) {
                Resources resources = context.injector().getResources();
                ConfigSection config = requireInstance(instance, ConfigSection.class, ConfigFile.class);

                configService.create(config.getClass());
                resources.on(instance.getClass())
                        .assignInstance(instance);

                for (Field field : config.getClass().getFields()) {
                    try {
                        Object value = field.get(config);
                        if (value != null) {
                            resources.on(field.getType())
                                    .assignInstance(value);
                        }
                    }
                    catch (IllegalAccessException ignored) {}
                }
            }

            @Override

            public Class<ConfigFile> annotation() {
                return ConfigFile.class;
            }
    }

    @Inject
    private record PlaceholderProcessor(PlaceholderService placeholderService)
            implements ComponentProcessor<Placeholder> {

        @Override
        public void process(Object instance, Placeholder annotation, ComponentProcessorContext context) {
            PluginPlaceholder pluginPlaceholder = requireInstance(instance, PluginPlaceholder.class, Placeholder.class);
            placeholderService.register(pluginPlaceholder);
        }


        @Override
        public Class<Placeholder> annotation() {
            return Placeholder.class;
        }
    }

    @Inject
    private record LiteHandlerProcessor(LiteCommandsConfigurer liteCommandsConfigurer)
            implements ComponentProcessor<LiteHandler> {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void process(
                Object instance,
                LiteHandler annotation,
                ComponentProcessorContext context
        ) {

            LiteCommandsBuilder<?, ?, ?> builder = liteCommandsConfigurer.builder();
            ResultHandler resultHandler = requireInstance(instance, ResultHandler.class, LiteHandler.class);

            if (resultHandler instanceof InvalidUsageHandler invalidUsageHandler) {
                builder.invalidUsage(invalidUsageHandler);
                return;
            }

            if (resultHandler instanceof MissingPermissionsHandler missingPermissionsHandler) {
                builder.missingPermission(missingPermissionsHandler);
                return;
            }

            builder.result(annotation.value(), resultHandler);
        }


        @Override
        public Class<LiteHandler> annotation() {
            return LiteHandler.class;
        }
    }

    @Inject
    private record LiteCommandProcessor(LiteCommandsConfigurer liteCommandsConfigurer)
            implements ComponentProcessor<LiteCommand> {

        @Override
        public void process(
                Object instance,
                LiteCommand annotation,
                ComponentProcessorContext context
        ) {
            liteCommandsConfigurer.builder().commands(instance);
        }


        @Override
        public Class<LiteCommand> annotation() {
            return LiteCommand.class;
        }
    }

    @Inject
    private record LiteArgumentProcessor(LiteCommandsConfigurer liteCommandsConfigurer)
            implements ComponentProcessor<LiteArgument> {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void process(
                Object instance,
                LiteArgument annotation,
                ComponentProcessorContext context
        ) {
            ArgumentResolver argumentResolver = requireInstance(instance, ArgumentResolver.class, LiteArgument.class);

            LiteCommandsBuilder<?, ?, ?> builder = liteCommandsConfigurer.builder();
            Class<?> argumentClass = annotation.type();
            String argumentKey = annotation.key();

            builder.argument(argumentClass, ArgumentKey.of(argumentKey), argumentResolver);
        }


        @Override
        public Class<LiteArgument> annotation() {
            return LiteArgument.class;
        }
    }

    @Inject
    private record LiteContextProcessor(LiteCommandsConfigurer liteCommandsConfigurer)
            implements ComponentProcessor<LiteContext> {


        @Override
        public Class<LiteContext> annotation() {
            return LiteContext.class;
        }

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void process(Object instance, LiteContext annotation, ComponentProcessorContext context) {
            ContextProvider contextProvider = requireInstance(instance, ContextProvider.class, LiteContext.class);

            LiteCommandsBuilder<?, ?, ?> builder = liteCommandsConfigurer.builder();
            Class<?> contextClass = annotation.type();

            builder.context(contextClass, contextProvider);
        }
    }
}
