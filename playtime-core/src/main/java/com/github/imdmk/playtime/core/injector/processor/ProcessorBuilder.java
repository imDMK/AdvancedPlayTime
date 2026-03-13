package com.github.imdmk.playtime.core.injector.processor;

import org.jetbrains.annotations.CheckReturnValue;

import java.lang.annotation.Annotation;

public final class ProcessorBuilder<A extends Annotation> {

    private final Class<A> annotation;
    private ProcessorHandler<A> handler;

    private ProcessorBuilder(Class<A> annotation) {
        this.annotation = annotation;
    }

    public static <A extends Annotation> ProcessorBuilder<A> forAnnotation(
            Class<A> annotation
    ) {
        return new ProcessorBuilder<>(annotation);
    }

    public static <A extends Annotation> ProcessorBuilder<A> of(
            Class<A> annotation,
            Class<? extends ComponentProcessor<A>> processor
    ) {
        return new ProcessorBuilder<>(annotation).processor(processor);
    }

    @CheckReturnValue
    public ProcessorBuilder<A> handle(ProcessorHandler<A> handler) {
        this.handler = handler;
        return this;
    }

    @CheckReturnValue
    public ProcessorBuilder<A> processor(ComponentProcessor<A> processor) {
        this.handler = processor::process;
        return this;
    }

    @CheckReturnValue
    public ProcessorBuilder<A> processor(Class<? extends ComponentProcessor<A>> processorClass) {
        this.handler = (instance, annotation, ctx) ->
                ctx.injector()
                        .newInstance(processorClass)
                        .process(instance, annotation, ctx);
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