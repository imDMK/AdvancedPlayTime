package com.github.imdmk.playtime.injector;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

final class ComponentScanner {

    private static final String SHADED_LIBS = "com.github.imdmk.playtime.lib";

    private final String basePackage;

    ComponentScanner(@NotNull String basePackage) {
        this.basePackage = basePackage;
    }

    Set<Component<?>> scan(@NotNull Class<? extends Annotation> annotation) {
        try (final ScanResult scan = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(basePackage)
                .rejectPackages(SHADED_LIBS)
                .scan()) {

            return scan.getClassesWithAnnotation(annotation.getName())
                    .stream()
                    .map(ClassInfo::loadClass)
                    .filter(this::isValidComponent)
                    .map(type -> new Component<>(
                            type,
                            type.getAnnotation(annotation)
                    ))
                    .collect(Collectors.toSet());
        }
    }

    private boolean isValidComponent(@NotNull Class<?> type) {
        return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
    }
}


