package com.github.imdmk.playtime.injector;

import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.lang.annotation.Annotation;

public final class Component<A extends Annotation> {

    private final Class<?> type;
    private final A annotation;
    private Object instance;

    public Component(@NotNull Class<?> type, @NotNull A annotation) {
        this.type = type;
        this.annotation = annotation;
    }

    public Class<?> type() {
        return type;
    }

    public A annotation() {
        return annotation;
    }

    public Object instance() {
        return instance;
    }

    public void create(@NotNull Injector injector) {
        if (this.instance == null) {
            this.instance = injector.newInstance(type);
        }
    }
}




