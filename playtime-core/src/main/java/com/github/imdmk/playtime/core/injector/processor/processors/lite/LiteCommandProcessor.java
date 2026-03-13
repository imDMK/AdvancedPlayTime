package com.github.imdmk.playtime.core.injector.processor.processors.lite;

import com.github.imdmk.playtime.core.injector.annotations.lite.LiteCommand;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.core.platform.command.LiteCommandsConfigurer;

public final class LiteCommandProcessor implements ComponentProcessor<LiteCommand> {

    private final LiteCommandsConfigurer liteCommandsConfigurer;

    public LiteCommandProcessor(LiteCommandsConfigurer liteCommandsConfigurer) {
        this.liteCommandsConfigurer = liteCommandsConfigurer;
    }

    @Override
    public Class<LiteCommand> annotation() {
        return LiteCommand.class;
    }

    @Override
    public void process(
            Object instance,
            LiteCommand annotation,
            ComponentProcessorContext context
    ) {
        liteCommandsConfigurer.builder().commands(instance);
    }
}
