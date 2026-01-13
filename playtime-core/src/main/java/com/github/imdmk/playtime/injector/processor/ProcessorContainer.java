package com.github.imdmk.playtime.injector.processor;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public record ProcessorContainer<A extends Annotation>(
        @NotNull Class<A> annotationType,
        @NotNull ComponentProcessor<A> processor
) {}
