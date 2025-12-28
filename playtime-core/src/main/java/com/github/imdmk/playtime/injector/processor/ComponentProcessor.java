package com.github.imdmk.playtime.injector.processor;

import com.github.imdmk.playtime.injector.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public interface ComponentProcessor<A extends Annotation> {

    @NotNull Class<A> annotation();

    void process(@NotNull Component<A> component, @NotNull ComponentProcessorContext context);

}

