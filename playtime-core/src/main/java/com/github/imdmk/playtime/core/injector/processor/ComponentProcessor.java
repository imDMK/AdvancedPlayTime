package com.github.imdmk.playtime.core.injector.processor;

import java.lang.annotation.Annotation;

public interface ComponentProcessor<A extends Annotation> {

    Class<A> annotation();

    void process(
            Object instance,
            A annotation,
            ComponentProcessorContext context
    );

    default <T> T requireInstance(
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
}


