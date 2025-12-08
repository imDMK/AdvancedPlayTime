package com.github.imdmk.playtime;

import com.github.imdmk.playtime.infrastructure.injector.Bind;
import com.github.imdmk.playtime.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Discovers fields in {@link PlayTimePlugin} annotated with {@link Bind}
 * and registers their instances into the DI {@link Resources}.
 * <p>
 * This approach keeps {@link PlayTimePlugin} focused on lifecycle/bootstrap logic
 * while delegating dependency wiring to a dedicated, reflection-based binder.
 * Only non-static fields with {@code @Bind} are processed.
 */
final class PlayTimeBinder {

    private final PlayTimePlugin core;

    /**
     * Creates a new binder for the given plugin instance.
     *
     * @param core the plugin root object providing core dependencies
     */
    PlayTimeBinder(@NotNull PlayTimePlugin core) {
        this.core = Validator.notNull(core, "core");
    }

    /**
     * Scans the {@link PlayTimePlugin} class hierarchy, locates fields annotated with
     * {@link Bind}, reads their values, and registers them into the provided
     * {@link Resources} instance.
     *
     * @param resources DI container resources to bind into
     */
    void bind(@NotNull Resources resources) {
        Validator.notNull(resources, "resources");

        Class<?> type = core.getClass();

        while (type != null && type != Object.class) {
            for (Field field : type.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Bind.class)) {
                    continue;
                }

                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                final Object value;
                try {
                    value = field.get(core);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to access @BindCore field: " + field, e);
                }

                if (value == null) {
                    throw new IllegalStateException("@BindCore field " + field + " is null during binding");
                }

                resources.on(field.getType()).assignInstance(value);
            }

            type = type.getSuperclass();
        }

        // Provide Injector via lazy supplier
        resources.on(Injector.class).assignInstance(() -> core.injector);
    }
}
