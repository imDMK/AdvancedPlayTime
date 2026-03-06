package com.github.imdmk.playtime.core.injector.processor;

import java.lang.annotation.Annotation;

public record ProcessorContainer<A extends Annotation>(
        Class<A> annotationType,
        ComponentProcessor<A> processor
) {}
