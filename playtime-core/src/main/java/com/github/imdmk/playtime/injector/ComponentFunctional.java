package com.github.imdmk.playtime.injector;

import com.github.imdmk.playtime.injector.processor.ComponentProcessorContext;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface ComponentFunctional<T, A extends Annotation> {

    void accept(
            @NotNull T instance,
            @NotNull A annotation,
            @NotNull ComponentProcessorContext context
    );
}

