package com.github.imdmk.playtime.core.injector;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;

final class ComponentScanner {

    private static final String SHADED_LIBS = "com.github.imdmk.playtime.lib";

    private final String basePackage;
    private final ComponentFactory componentFactory;

    ComponentScanner(String basePackage) {
        this.basePackage = basePackage;
        this.componentFactory = new ComponentFactory();
    }

    <A extends Annotation> List<Component<A>> scan(Class<A> annotationType) {
        try (ScanResult scan = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(basePackage)
                .rejectPackages(SHADED_LIBS)
                .scan()) {

            return scan.getClassesWithAnnotation(annotationType.getName())
                    .stream()
                    .map(ClassInfo::loadClass)
                    .filter(ComponentScanner::isValidComponent)
                    .map(type -> componentFactory.create(type, annotationType))
                    .toList();
        }
    }

    private static boolean isValidComponent(Class<?> type) {
        return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
    }
}


