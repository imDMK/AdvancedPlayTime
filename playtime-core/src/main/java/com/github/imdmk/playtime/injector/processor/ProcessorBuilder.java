package com.github.imdmk.playtime.injector.processor;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public final class ProcessorBuilder<A extends Annotation> {

    private final Class<A> annotation;

    private ProcessorHandler<A> handler;

    private ProcessorBuilder(@NotNull Class<A> annotation) {
        this.annotation = annotation;
    }

    public static <A extends Annotation> ProcessorBuilder<A> forAnnotation(@NotNull Class<A> annotation) {
        return new ProcessorBuilder<>(annotation);
    }

    @CheckReturnValue
    public ProcessorBuilder<A> handle(@NotNull ProcessorHandler<A> handler) {
        this.handler = handler;
        return this;
    }

    public ProcessorContainer<A> build() {
        if (handler == null) {
            throw new IllegalStateException(
                    "Processor for @" + annotation.getSimpleName() + " has no handler defined"
            );
        }

        return new ProcessorContainer<>(
                annotation,
                new ComponentProcessorFunctional<>(annotation, handler)
        );
    }
}
