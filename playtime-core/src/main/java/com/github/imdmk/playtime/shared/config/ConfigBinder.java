package com.github.imdmk.playtime.shared.config;

import eu.okaeri.configs.OkaeriConfig;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Utility for binding Okaeri configuration objects to Panda {@link Resources} injection context.
 *
 * <p>This binder walks over provided configuration sections, registers each instance in the
 * {@link Resources} registry via {@code assignInstance}, and recursively processes nested
 * {@link OkaeriConfig} fields. Static and transient fields are ignored. The traversal is
 * cycle-safe through identity-based visited tracking.</p>
 *
 * <p><strong>Thread-safety:</strong> This class holds no state, but {@link Resources} and
 * config instances are typically not thread-safe for concurrent mutation. Bind on the init thread.</p>
 */
public final class ConfigBinder {

    private ConfigBinder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Binds the given configuration sections into the {@link Resources} context.
     *
     * <p>Each section (and its nested {@link OkaeriConfig} fields) is registered
     * with {@link Resources#on(Class)} → {@code assignInstance(object)}. Cycles are
     * prevented using an identity-based visited set.</p>
     *
     * @param resources non-null Panda resources/injector context
     * @param sections  non-null set of top-level configuration sections to bind
     * @throws IllegalStateException if reflective access to a config field fails
     * @throws NullPointerException  if any argument is null
     */
    public static void bind(@NotNull Resources resources, @NotNull Set<ConfigSection> sections) {
        final Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        for (final var section : sections) {
            bindRecursive(resources, section, visited);
        }
    }

    /**
     * Recursively registers the given object and its nested {@link OkaeriConfig} fields
     * into the {@link Resources} context.
     *
     * <p>Walks the class hierarchy up to {@link Object}, skipping {@code static} and {@code transient}
     * fields. Uses identity tracking to avoid infinite recursion on cyclic graphs.</p>
     *
     * @param resources non-null injection context
     * @param object    non-null configuration object to bind
     * @param visited   identity set used to detect already-processed instances
     * @throws IllegalStateException if reflective access fails
     */
    private static void bindRecursive(@NotNull Resources resources, @NotNull Object object, @NotNull Set<Object> visited) {
        if (!visited.add(object)) {
            return;
        }

        resources.on(object.getClass()).assignInstance(object);

        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (value instanceof OkaeriConfig nested) {
                        bindRecursive(resources, nested, visited);
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to bind config field: "
                            + clazz.getSimpleName() + "#" + field.getName(), e);
                }
            }
        }
    }
}
