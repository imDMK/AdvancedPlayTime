package com.github.imdmk.playtime.injector;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

final class ComponentFactory {

    <A extends Annotation> Component<A> create(
            @NotNull Class<?> type,
            @NotNull Class<A> annotationType
    ) {
        final A annotation = type.getAnnotation(annotationType);

        final ComponentPriority componentPriority = extractPriority(annotation);
        final int order = extractOrder(annotation);

        return new Component<>(type, annotation, componentPriority, order);
    }

    private ComponentPriority extractPriority(Annotation annotation) {
        try {
            return (ComponentPriority) annotation.annotationType()
                    .getMethod("priority")
                    .invoke(annotation);
        } catch (Exception e) {
            return ComponentPriority.NORMAL;
        }
    }

    private int extractOrder(Annotation annotation) {
        try {
            return (int) annotation.annotationType()
                    .getMethod("order")
                    .invoke(annotation);
        } catch (Exception e) {
            return 0;
        }
    }
}
