package com.github.imdmk.playtime.injector.priority;

import com.github.imdmk.playtime.injector.Component;
import com.github.imdmk.playtime.injector.annotations.NoneAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class AnnotationPriorityProvider implements PriorityProvider {

    @Override
    public Priority apply(Component component) {
        final Class<?> componentClass = component.type();

        if (component.annotation().annotationType() == NoneAnnotation.class) {
            return Priority.HIGHEST;
        }

        for (final Annotation annotation : componentClass.getAnnotations()) {
            try {
                final Method method = annotation.annotationType().getMethod("priority");

                final Object value = method.invoke(annotation);
                if (value instanceof Priority priority) {
                    return priority;
                }
            }
            catch (NoSuchMethodException ignored) {
                // doesn't support priority - skip
            }
            catch (ReflectiveOperationException e) {
                throw new IllegalStateException(
                        "Failed to resolve priority for " + componentClass.getName(), e
                );
            }
        }

        return Priority.NORMAL;
    }
}
