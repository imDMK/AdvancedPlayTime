package com.github.imdmk.playtime.core.injector.processor;

import java.lang.annotation.Annotation;

public final class ComponentProcessorFunctional<A extends Annotation>
        implements ComponentProcessor<A> {

    private final Class<A> annotation;
    private final ProcessorHandler<A> handler;

    public ComponentProcessorFunctional(
            Class<A> annotation,
            ProcessorHandler<A> handler
    ) {
        this.annotation = annotation;
        this.handler = handler;
    }

    @Override
    public Class<A> annotation() {
        return annotation;
    }

    @Override
    public void process(
            Object instance,
            A annotation,
            ComponentProcessorContext context
    ) {
        handler.handle(instance, annotation, context);
    }
}
