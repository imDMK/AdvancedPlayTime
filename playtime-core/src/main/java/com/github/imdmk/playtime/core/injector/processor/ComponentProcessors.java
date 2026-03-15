package com.github.imdmk.playtime.core.injector.processor;

import com.github.imdmk.playtime.core.injector.annotations.*;
import com.github.imdmk.playtime.core.injector.annotations.gui.Gui;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteArgument;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteContext;
import com.github.imdmk.playtime.core.injector.annotations.lite.LiteHandler;
import com.github.imdmk.playtime.core.injector.annotations.placeholder.Placeholder;
import com.github.imdmk.playtime.core.injector.processor.processors.*;
import com.github.imdmk.playtime.core.injector.processor.processors.gui.GuiProcessor;
import com.github.imdmk.playtime.core.injector.processor.processors.lite.LiteArgumentProcessor;
import com.github.imdmk.playtime.core.injector.processor.processors.lite.LiteCommandProcessor;
import com.github.imdmk.playtime.core.injector.processor.processors.lite.LiteContextProcessor;
import com.github.imdmk.playtime.core.injector.processor.processors.lite.LiteHandlerProcessor;
import com.github.imdmk.playtime.core.injector.processor.processors.placeholder.PlaceholderProcessor;

import java.util.List;

public final class ComponentProcessors {

    private ComponentProcessors() {
        throw new UnsupportedOperationException("This is utility class and cannot be instantiated.");
    }

    public static List<ProcessorContainer<?>> defaults() {
        return List.of(
                ProcessorBuilder.of(ConfigFile.class, ConfigFileProcessor.class).build(),
                ProcessorBuilder.of(Service.class, ServiceProcessor.class).build(),
                ProcessorBuilder.of(Database.class, DatabaseProcessor.class).build(),
                ProcessorBuilder.of(Repository.class, RepositoryProcessor.class).build(),
                ProcessorBuilder.of(PluginListener.class, PluginListenerProcessor.class).build(),
                ProcessorBuilder.of(Task.class, TaskProcessor.class).build(),
                ProcessorBuilder.of(Gui.class, GuiProcessor.class).build(),
                ProcessorBuilder.of(Placeholder.class, PlaceholderProcessor.class).build(),
                ProcessorBuilder.of(LiteCommand.class, LiteCommandProcessor.class).build(),
                ProcessorBuilder.of(LiteArgument.class, LiteArgumentProcessor.class).build(),
                ProcessorBuilder.of(LiteContext.class, LiteContextProcessor.class).build(),
                ProcessorBuilder.of(LiteHandler.class, LiteHandlerProcessor.class).build()
        );
    }
}
