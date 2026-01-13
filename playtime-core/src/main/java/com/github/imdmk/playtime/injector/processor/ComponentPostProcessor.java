package com.github.imdmk.playtime.injector.processor;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ComponentPostProcessor {

    void postProcess(@NotNull Object instance, @NotNull ComponentProcessorContext context);

}


