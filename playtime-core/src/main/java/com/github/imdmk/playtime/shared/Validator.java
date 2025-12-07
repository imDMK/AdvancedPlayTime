package com.github.imdmk.playtime.shared;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
     * <p>
     * This method is typically used to validate constructor arguments and
     * configuration values. If the supplied object is non-null, it is returned
     * unchanged; otherwise a {@link NullPointerException} is thrown with the
     * provided message.
     *
     * @param obj     the value to validate; may be null
     * @param context context of exception used when {@code obj} is null
     * @param <T>     type of the validated object
     * @return the non-null value of {@code obj}
     * @throws NullPointerException if {@code obj} is null
     */
    public static <T> T notNull(@Nullable T obj, @NotNull String context) {
        if (obj == null) {
            throw new NullPointerException(context + "");
        }
        return obj;
    }

    /**
     * Executes the given {@link Consumer} only if the supplied object is not {@code null}.
     * <p>
     * This helper is especially useful during shutdown or cleanup phases where
     * optional components may or may not be initialized. The consumer itself
     * must be non-null; however, it will only be invoked when {@code obj} is non-null.
     *
     * <p>Example usage:
     * <pre>
     * Validator.ifNotNull(taskScheduler, TaskScheduler::shutdown);
     * Validator.ifNotNull(messageService, MessageService::shutdown);
     * </pre>
     *
     * @param obj      the object to check before executing the consumer; may be null
     * @param consumer operation to execute when {@code obj} is non-null (never null)
     * @param <T>      type of the object passed to the consumer
     * @throws NullPointerException if {@code consumer} is null
     */
    public static <T> void ifNotNull(@Nullable T obj, @NotNull Consumer<T> consumer) {
        Validator.notNull(consumer, "consumer is null");
        if (obj != null) {
            consumer.accept(obj);
        }
    }

}
