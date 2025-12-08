package com.github.imdmk.playtime.config;

import eu.okaeri.configs.OkaeriConfig;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class InjectorConfigBinder {

    private InjectorConfigBinder() {
        throw new UnsupportedOperationException("This is utility class and cannot be instantiated.");
    }

    public static void bind(@NotNull Resources resources, @NotNull Set<ConfigSection> sections) {
        final Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        for (final var section : sections) {
            bindRecursive(resources, section, visited);
        }
    }

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
