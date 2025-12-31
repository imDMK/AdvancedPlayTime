package com.github.imdmk.playtime.injector;

import com.github.imdmk.playtime.injector.annotations.NoneAnnotation;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

final class ComponentScanner {

    private final Reflections reflections;

    ComponentScanner(@NotNull String basePackage) {
        this.reflections = new Reflections(basePackage);
    }

    Set<Component<?>> scan(@NotNull Class<? extends Annotation> annotation) {
        if (annotation == NoneAnnotation.class) {
            return reflections.getSubTypesOf(Object.class).stream()
                    .filter(type -> !type.isInterface())
                    .filter(type -> !Modifier.isAbstract(type.getModifiers()))
                    .map(type -> new Component<>(
                            type,
                            NoneAnnotation.INSTANCE
                    ))
                    .collect(Collectors.toSet());
        }

        return reflections.getTypesAnnotatedWith(annotation).stream()
                .map(type -> new Component<>(
                        type,
                        type.getAnnotation(annotation)
                ))
                .collect(Collectors.toSet());
    }
}

