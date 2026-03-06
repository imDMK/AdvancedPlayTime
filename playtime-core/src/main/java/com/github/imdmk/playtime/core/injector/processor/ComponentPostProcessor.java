package com.github.imdmk.playtime.core.injector.processor;

@FunctionalInterface
public interface ComponentPostProcessor {

    void postProcess(Object instance, ComponentProcessorContext context);

}


