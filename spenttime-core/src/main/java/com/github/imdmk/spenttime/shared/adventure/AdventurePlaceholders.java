package com.github.imdmk.spenttime.shared.adventure;

import com.github.imdmk.spenttime.shared.Validator;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable container mapping literal placeholder keys to Adventure {@link Component} values.
 * <p>
 * Instances are created via the {@link Builder}. Once built, the mapping is read-only.
 * <p>
 * <strong>Thread-safety:</strong> Fully immutable and safe for concurrent use.
 */
public final class AdventurePlaceholders {

    private static final AdventurePlaceholders EMPTY = new AdventurePlaceholders(Map.of());

    private final Map<String, Component> map;

    private AdventurePlaceholders(@NotNull Map<String, Component> map) {
        Validator.notNull(map, "map cannot be null");
        this.map = Collections.unmodifiableMap(map);
    }

    /**
     * Returns an unmodifiable view of all placeholder mappings.
     *
     * @return unmodifiable placeholder map
     */
    @Unmodifiable
    @NotNull
    public Map<String, Component> asMap() {
        return map;
    }

    /**
     * Returns the number of registered placeholders.
     *
     * @return placeholder count
     */
    public int size() {
        return map.size();
    }

    /**
     * Checks if the placeholder map is empty.
     *
     * @return {@code true} if no placeholders are defined
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns a shared immutable empty instance.
     *
     * @return empty placeholder container
     */
    public static @NotNull AdventurePlaceholders empty() {
        return EMPTY;
    }

    /**
     * Creates a new builder for {@link AdventurePlaceholders}.
     *
     * @return new builder instance
     */
    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for {@link AdventurePlaceholders}.
     */
    public static final class Builder {

        private final Map<String, Component> entries = new LinkedHashMap<>();

        /**
         * Adds a literal → component mapping.
         *
         * @param key   literal placeholder key
         * @param value replacement component
         * @return this builder for chaining
         */
        @Contract("_,_ -> this")
        public @NotNull Builder with(@NotNull String key, @NotNull Component value) {
            Validator.notNull(key, "key cannot be null");
            Validator.notNull(value, "value cannot be null");
            this.entries.put(key, value);
            return this;
        }

        /**
         * Adds a literal → plain text mapping (converted to {@link Component#text(String)}).
         *
         * @param key   literal placeholder key
         * @param value replacement text
         * @return this builder for chaining
         */
        @Contract("_,_ -> this")
        public @NotNull Builder with(@NotNull String key, @NotNull String value) {
            Validator.notNull(key, "key cannot be null");
            Validator.notNull(value, "value cannot be null");
            this.entries.put(key, Component.text(value));
            return this;
        }

        /**
         * Adds all entries from another {@link AdventurePlaceholders}.
         *
         * @param other another placeholder container
         * @return this builder for chaining
         */
        @Contract("_ -> this")
        public @NotNull Builder with(@NotNull AdventurePlaceholders other) {
            Validator.notNull(other, "other cannot be null");
            this.entries.putAll(other.asMap());
            return this;
        }

        /**
         * Adds a placeholder using any object value.
         * The value is converted to plain text via {@link String#valueOf(Object)}.
         *
         * @param key   placeholder key
         * @param value object to convert and insert
         * @return this builder for chaining
         * @throws NullPointerException if key or value is null
         */
        @Contract("_,_ -> this")
        public @NotNull Builder with(@NotNull String key, @NotNull Object value) {
            Validator.notNull(key, "key cannot be null");
            Validator.notNull(value, "value cannot be null");
            this.entries.put(key, Component.text(String.valueOf(value)));
            return this;
        }

        /**
         * Builds an immutable {@link AdventurePlaceholders} instance.
         *
         * @return immutable placeholder container
         */
        public @NotNull AdventurePlaceholders build() {
            if (this.entries.isEmpty()) {
                return EMPTY;
            }

            return new AdventurePlaceholders(new LinkedHashMap<>(this.entries));
        }
    }
}
