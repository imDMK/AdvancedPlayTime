package com.github.imdmk.playtime.core.injector.processor.processors.lite;

import com.github.imdmk.playtime.core.injector.annotations.lite.LiteArgument;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.core.platform.command.LiteCommandsConfigurer;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;

public final class LiteArgumentProcessor implements ComponentProcessor<LiteArgument> {

    private final LiteCommandsConfigurer liteCommandsConfigurer;

    public LiteArgumentProcessor(LiteCommandsConfigurer liteCommandsConfigurer) {
        this.liteCommandsConfigurer = liteCommandsConfigurer;
    }

    @Override
    public Class<LiteArgument> annotation() {
        return LiteArgument.class;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void process(
            Object instance,
            LiteArgument annotation,
            ComponentProcessorContext context
    ) {
        ArgumentResolver resolver = requireInstance(
                instance,
                ArgumentResolver.class,
                LiteArgument.class
        );

        LiteCommandsBuilder<?, ?, ?> builder = liteCommandsConfigurer.builder();
        Class<?> argumentClass = annotation.type();
        String argumentKey = annotation.key();

        builder.argument(argumentClass, ArgumentKey.of(argumentKey), resolver);
    }
}
