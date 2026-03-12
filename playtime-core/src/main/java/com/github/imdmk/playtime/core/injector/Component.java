package com.github.imdmk.playtime.core.injector;

import org.panda_lang.utilities.inject.DependencyInjectionException;
import org.panda_lang.utilities.inject.Injector;

import java.lang.annotation.Annotation;

public final class Component<A extends Annotation> {

    private final Class<?> type;
    private final A annotation;
    private final ComponentPriority componentPriority;
    private final int order;

    private Object instance;

    public Component(
            Class<?> type,
            A annotation,
            ComponentPriority componentPriority,
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
        if (instance == null) {
            throw new IllegalStateException("Component instance is not created yet.");
        }

        return instance;
    }

    public void createInstance(Injector injector) throws DependencyInjectionException {
        if (instance != null) {
            throw new IllegalStateException("Component instance already created.");
        }

        instance = injector.newInstance(type);
    }
}





