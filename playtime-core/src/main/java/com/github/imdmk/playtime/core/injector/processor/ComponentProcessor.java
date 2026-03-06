package com.github.imdmk.playtime.core.injector.processor;

import java.lang.annotation.Annotation;

public interface ComponentProcessor<A extends Annotation> {

    Class<A> annotation();

    void process(
            Object instance,
            A annotation,
            ComponentProcessorContext context
    );
}


