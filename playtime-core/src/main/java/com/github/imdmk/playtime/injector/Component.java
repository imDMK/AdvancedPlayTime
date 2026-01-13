package com.github.imdmk.playtime.injector;

import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.lang.annotation.Annotation;

public final class Component<A extends Annotation> {

    private final Class<?> type;
    private final A annotation;
    private final ComponentPriority componentPriority;
    private final int order;

    private Object instance;

    public Component(
            @NotNull Class<?> type,
            @NotNull A annotation,
            @NotNull ComponentPriority componentPriority,
            int order
    ) {
        this.type = type;
        this.annotation = annotation;
        this.componentPriority = componentPriority;
        this.order = order;
    }

    public Class<?> type() {
        return type;
    }

    public A annotation() {
        return annotation;
    }

    public ComponentPriority priority() {
        return componentPriority;
    }

    public int order() {
        return order;
    }

    public Object instance() {
        return instance;
    }

    public void createInstance(@NotNull Injector injector) {
        if (instance == null) {
            instance = injector.newInstance(type);
        }
    }
}





