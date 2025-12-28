package com.github.imdmk.playtime.injector.processor;

import com.github.imdmk.playtime.injector.Component;
import com.github.imdmk.playtime.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public abstract class AbstractComponentProcessor<A extends Annotation>
        implements ComponentProcessor<A> {

    @Override
    public void process(@NotNull Component<A> component, @NotNull ComponentProcessorContext context) {
        Validator.notNull(component, "component");
        Validator.notNull(context, "context");

        final Object instance = context.injector().newInstance(component.type());
        this.handle(instance, component.annotation(), context);
    }

    protected abstract void handle(
            @NotNull Object instance,
            @NotNull A annotation,
            @NotNull ComponentProcessorContext context
    );
}


