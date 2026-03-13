package com.github.imdmk.playtime.core.injector.processor.processors.lite;

import com.github.imdmk.playtime.core.injector.annotations.lite.LiteContext;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.core.platform.command.LiteCommandsConfigurer;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.context.ContextProvider;

public final class LiteContextProcessor implements ComponentProcessor<LiteContext> {

    private final LiteCommandsConfigurer liteCommandsConfigurer;

    public LiteContextProcessor(LiteCommandsConfigurer liteCommandsConfigurer) {
        this.liteCommandsConfigurer = liteCommandsConfigurer;
    }

    @Override
    public Class<LiteContext> annotation() {
        return LiteContext.class;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void process(
            Object instance,
            LiteContext annotation,
            ComponentProcessorContext context
    ) {
        ContextProvider contextProvider = requireInstance(
                instance,
                ContextProvider.class,
                LiteContext.class
        );

        LiteCommandsBuilder<?, ?, ?> builder = liteCommandsConfigurer.builder();
        Class<?> contextClass = annotation.type();

        builder.context(contextClass, contextProvider);
    }
}
