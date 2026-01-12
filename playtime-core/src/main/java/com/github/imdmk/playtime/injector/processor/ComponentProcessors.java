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
import com.github.imdmk.playtime.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.injector.annotations.lite.LiteHandler;
import com.github.imdmk.playtime.injector.annotations.placeholderapi.Placeholder;
import com.github.imdmk.playtime.platform.gui.GuiRegistry;
import com.github.imdmk.playtime.platform.gui.IdentifiableGui;
import com.github.imdmk.playtime.platform.litecommands.LiteCommandsConfigurer;
import com.github.imdmk.playtime.platform.placeholder.PlaceholderService;
import com.github.imdmk.playtime.platform.placeholder.PluginPlaceholder;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Resources;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.lang.reflect.Field;
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
                            final DatabaseBootstrap databaseBootstrap = requireInstance(instance, DatabaseBootstrap.class, Database.class);

                            databaseBootstrap.start();
                            ctx.injector().getResources()
                                    .on(DatabaseBootstrap.class)
                                    .assignInstance(databaseBootstrap);
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(Repository.class)
                        .handle((instance, annotation, ctx) -> {
                            final OrmLiteRepository<?, ?> repository = requireInstance(instance, OrmLiteRepository.class, OrmLiteRepository.class);

                            repository.start();
                            ctx.injector().getResources()
                                    .on(repository.getClass())
                                    .assignInstance(repository);
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(Controller.class)
                        .handle((instance, annotation, ctx) -> {
                            final Listener listener = requireInstance(instance, Listener.class, Controller.class);
                            plugin.getServer()
                                    .getPluginManager()
                                    .registerEvents(listener, plugin);
                        })
                        .build(),

                ProcessorBuilder.forAnnotation(Gui.class)
                        .handle((instance, annotation, ctx) -> ctx.injector().newInstance(GuiProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(Placeholder.class)
                        .handle((instance, annotation, ctx) -> ctx.injector().newInstance(PlaceholderProcessor.class).process(instance, annotation, ctx))
                        .build(),

                ProcessorBuilder.forAnnotation(LiteCommand.class)
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
    private record GuiProcessor(@NotNull GuiRegistry guiRegistry)
            implements ComponentProcessor<Gui> {

        @Override
        public void process(@NotNull Object instance, @NotNull Gui annotation, @NotNull ComponentProcessorContext context) {
            final IdentifiableGui identifiableGui = requireInstance(instance, IdentifiableGui.class, Gui.class);
            guiRegistry.register(identifiableGui);
        }

        @NotNull
        @Override
        public Class<Gui> annotation() {
            return Gui.class;
        }
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
                final Resources resources = context.injector().getResources();
                final ConfigSection config = requireInstance(instance, ConfigSection.class, ConfigFile.class);

                configService.create(config.getClass());
                resources.on(instance.getClass())
                        .assignInstance(instance);

                for (final Field field : config.getClass().getFields()) {
                    try {
                        final Object value = field.get(config);
                        if (value != null) {
                            resources.on(field.getType())
                                    .assignInstance(value);
                        }
                    }
                    catch (IllegalAccessException ignored) {}
                }
            }

            @Override
            @NotNull
            public Class<ConfigFile> annotation() {
                return ConfigFile.class;
            }
    }

    @Inject
    private record PlaceholderProcessor(@NotNull PlaceholderService placeholderService)
            implements ComponentProcessor<Placeholder> {

        @Override
        public void process(@NotNull Object instance, @NotNull Placeholder annotation, @NotNull ComponentProcessorContext context) {
            final PluginPlaceholder pluginPlaceholder = requireInstance(instance, PluginPlaceholder.class, Placeholder.class);
            placeholderService.register(pluginPlaceholder);
        }

        @NotNull
        @Override
        public Class<Placeholder> annotation() {
            return Placeholder.class;
        }
    }

    @Inject
    private record LiteHandlerProcessor(@NotNull LiteCommandsConfigurer liteCommandsConfigurer)
            implements ComponentProcessor<LiteHandler> {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void process(
                @NotNull Object instance,
                @NotNull LiteHandler annotation,
                @NotNull ComponentProcessorContext context
        ) {

            final LiteCommandsBuilder<?, ?, ?> builder = liteCommandsConfigurer.builder();
            final ResultHandler resultHandler = requireInstance(instance, ResultHandler.class, LiteHandler.class);

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

        @NotNull
        @Override
        public Class<LiteHandler> annotation() {
            return LiteHandler.class;
        }
    }

    @Inject
    private record LiteCommandProcessor(@NotNull LiteCommandsConfigurer liteCommandsConfigurer)
            implements ComponentProcessor<LiteCommand> {

        @Override
        public void process(
                @NotNull Object instance,
                @NotNull LiteCommand annotation,
                @NotNull ComponentProcessorContext context
        ) {
            liteCommandsConfigurer.builder().commands(instance);
        }

        @NotNull
        @Override
        public Class<LiteCommand> annotation() {
            return LiteCommand.class;
        }
    }

    @Inject
    private record LiteArgumentProcessor(@NotNull LiteCommandsConfigurer liteCommandsConfigurer)
            implements ComponentProcessor<LiteArgument> {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void process(
                @NotNull Object instance,
                @NotNull LiteArgument annotation,
                @NotNull ComponentProcessorContext context
        ) {
            final ArgumentResolver argumentResolver = requireInstance(instance, ArgumentResolver.class, LiteArgument.class);

            final LiteCommandsBuilder<?, ?, ?> builder = liteCommandsConfigurer.builder();
            final Class<?> argumentClass = annotation.type();
            final String argumentKey = annotation.key();

            builder.argument(argumentClass, ArgumentKey.of(argumentKey), argumentResolver);
        }

        @NotNull
        @Override
        public Class<LiteArgument> annotation() {
            return LiteArgument.class;
        }
    }

}
