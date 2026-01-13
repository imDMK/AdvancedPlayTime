package com.github.imdmk.playtime.injector.processor;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public interface ComponentProcessor<A extends Annotation> {

    @NotNull Class<A> annotation();

    void process(
            @NotNull Object instance,
            @NotNull A annotation,
            @NotNull ComponentProcessorContext context
    );
}


