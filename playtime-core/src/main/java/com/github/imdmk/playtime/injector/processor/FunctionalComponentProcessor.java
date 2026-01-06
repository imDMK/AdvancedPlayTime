package com.github.imdmk.playtime.injector.processor;

import com.github.imdmk.playtime.injector.Component;
import com.github.imdmk.playtime.injector.ComponentFunctional;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public final class FunctionalComponentProcessor<T, A extends Annotation>
        implements ComponentProcessor<A> {

    private final Class<A> annotationType;
    private final Class<T> targetType;
    private final ComponentFunctional<T, A> consumer;

    public FunctionalComponentProcessor(
            Class<A> annotationType,
            Class<T> targetType,
            ComponentFunctional<T, A> consumer
    ) {
        this.annotationType = annotationType;
        this.targetType = targetType;
        this.consumer = consumer;
    }

    @Override
    public @NotNull Class<A> annotation() {
        return annotationType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(@NotNull Object instance, @NotNull A annotation, @NotNull ComponentProcessorContext context) {
        if (!targetType.isInstance(instance)) {
            return;
        }

        consumer.accept((T) instance, annotation, context);
    }
}


