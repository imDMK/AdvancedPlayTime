package com.github.imdmk.playtime.core.injector.processor;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface ProcessorHandler<A extends Annotation> {

    void handle(Object instance, A annotation, ComponentProcessorContext context);

}

