package com.github.imdmk.playtime.core.injector;

import java.lang.annotation.Annotation;

final class ComponentFactory {

    private static final ComponentPriority FALLBACK_PRIORITY = ComponentPriority.NORMAL;
    private static final int FALLBACK_ORDER = 0;

    <A extends Annotation> Component<A> create(
            Class<?> type,
            Class<A> annotationType
    ) {
        A annotation = type.getAnnotation(annotationType);

        ComponentPriority priority = extractPriority(annotation);
        int order = extractOrder(annotation);

        return new Component<>(type, annotation, priority, order);
    }

    private ComponentPriority extractPriority(Annotation annotation) {
        try {
            return (ComponentPriority) annotation.annotationType()
                    .getMethod("priority")
                    .invoke(annotation);
        } catch (ReflectiveOperationException e) {
            return FALLBACK_PRIORITY;
        }
    }

    private int extractOrder(Annotation annotation) {
        try {
            return (int) annotation.annotationType()
                    .getMethod("order")
                    .invoke(annotation);
        } catch (ReflectiveOperationException e) {
            return FALLBACK_ORDER;
        }
    }
}
