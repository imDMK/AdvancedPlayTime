package com.github.imdmk.playtime.core.injector.processor.processors.lite;

import com.github.imdmk.playtime.core.injector.annotations.lite.LiteHandler;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.core.platform.command.LiteCommandsConfigurer;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;

public final class LiteHandlerProcessor implements ComponentProcessor<LiteHandler> {

    private final LiteCommandsConfigurer liteCommandsConfigurer;

    public LiteHandlerProcessor(LiteCommandsConfigurer liteCommandsConfigurer) {
        this.liteCommandsConfigurer = liteCommandsConfigurer;
    }

    @Override
    public Class<LiteHandler> annotation() {
        return LiteHandler.class;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void process(
            Object instance,
            LiteHandler annotation,
            ComponentProcessorContext context
    ) {
        LiteCommandsBuilder<?, ?, ?> builder = liteCommandsConfigurer.builder();
        ResultHandler resultHandler = requireInstance(
                instance,
                ResultHandler.class,
                LiteHandler.class
        );

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
}
