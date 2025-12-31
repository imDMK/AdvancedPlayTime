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
            @NotNull Class<A> annotationType,
            @NotNull Class<T> targetType,
            @NotNull ComponentFunctional<T, A> consumer
    ) {
        this.annotationType = annotationType;
        this.targetType = targetType;
        this.consumer = consumer;
    }

    @Override
    public @NotNull Class<A> annotation() {
        return this.annotationType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(@NotNull Component<A> component, @NotNull ComponentProcessorContext context) {
        final Object instance = context.injector().newInstance(component.type());
        if (!targetType.isInstance(instance)) {
            return;
        }

        consumer.accept(
                (T) instance,
                component.annotation(),
                context
        );
    }
}

