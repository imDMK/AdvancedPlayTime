package com.github.imdmk.playtime.injector;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public record Component<A extends Annotation>(
        @NotNull Class<?> type,
        @NotNull A annotation
) {}



