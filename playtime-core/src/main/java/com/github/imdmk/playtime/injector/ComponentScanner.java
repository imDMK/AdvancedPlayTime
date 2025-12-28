package com.github.imdmk.playtime.injector;

import com.github.imdmk.playtime.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

final class ComponentScanner {

    private final Reflections reflections;

    ComponentScanner(@NotNull String basePackage) {
        Validator.notNull(basePackage, "basePackage");
        this.reflections = new Reflections(basePackage);
    }

    Set<Component<?>> scan(@NotNull Class<? extends Annotation> annotation) {
        Validator.notNull(annotation, "annotation");
        return reflections.getTypesAnnotatedWith(annotation).stream()
                .map(type -> new Component<>(
                        type,
                        type.getAnnotation(annotation)
                ))
                .collect(Collectors.toSet());
    }
}

