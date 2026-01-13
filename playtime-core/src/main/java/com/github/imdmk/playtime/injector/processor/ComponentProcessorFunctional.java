package com.github.imdmk.playtime.injector.processor;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public final class ComponentProcessorFunctional<A extends Annotation>
        implements ComponentProcessor<A> {

    private final Class<A> annotation;
    private final ProcessorHandler<A> handler;

    public ComponentProcessorFunctional(
            @NotNull Class<A> annotation,
            @NotNull ProcessorHandler<A> handler
    ) {
        this.annotation = annotation;
        this.handler = handler;
    }

    @NotNull
    @Override
    public Class<A> annotation() {
        return annotation;
    }

    @Override
    public void process(
            @NotNull Object instance,
            @NotNull A annotation,
            @NotNull ComponentProcessorContext context
    ) {
        handler.handle(instance, annotation, context);
    }
}
