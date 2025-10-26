package com.github.imdmk.spenttime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for common validation checks.
 * <p>
 * Provides null-safety guards used throughout the codebase.
 */
public final class Validator {

    private Validator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Ensures the given object is not {@code null}.
     *
     * @param obj     object to validate
     * @param message exception message if object is null
     * @param <T>     type of the validated object
     * @return the same object if not null
     * @throws NullPointerException if {@code obj} is null
     */
    public static <T> T notNull(@Nullable T obj, @NotNull String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }
}
