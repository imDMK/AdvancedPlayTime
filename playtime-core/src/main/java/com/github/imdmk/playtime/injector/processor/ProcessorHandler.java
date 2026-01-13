package com.github.imdmk.playtime.injector.processor;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface ProcessorHandler<A extends Annotation> {

    void handle(@NotNull Object instance, @NotNull A annotation, @NotNull ComponentProcessorContext context);

}

