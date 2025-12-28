package com.github.imdmk.playtime.injector.processor;

import com.github.imdmk.playtime.injector.Component;
import com.github.imdmk.playtime.injector.ComponentFunctional;
import com.github.imdmk.playtime.shared.validate.Validator;
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
        this.annotationType = Validator.notNull(annotationType, "annotationType");
        this.targetType = Validator.notNull(targetType, "targetType");
        this.consumer = Validator.notNull(consumer, "consumer");
    }

    @Override
    public @NotNull Class<A> annotation() {
        return this.annotationType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(@NotNull Component<A> component, @NotNull ComponentProcessorContext context) {
        Validator.notNull(component, "component");
        Validator.notNull(context, "context");

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

