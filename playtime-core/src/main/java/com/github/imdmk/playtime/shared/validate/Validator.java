package com.github.imdmk.playtime.shared.validate;

import java.util.function.Consumer;

public final class Validator {

    private Validator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> T notNull(T obj, String context) {
        if (obj == null) {
            throw new NullPointerException(context + " cannot be null");
        }
        return obj;
    }

    public static <T> void ifNotNull(T obj, Consumer<T> consumer) {
        if (obj != null) {
            consumer.accept(obj);
        }
    }

}
