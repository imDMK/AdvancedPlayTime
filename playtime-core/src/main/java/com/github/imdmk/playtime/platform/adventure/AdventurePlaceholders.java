package com.github.imdmk.playtime.platform.adventure;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AdventurePlaceholders {

    private static final AdventurePlaceholders EMPTY = new AdventurePlaceholders(Map.of());

    private final Map<String, Component> map;

    private AdventurePlaceholders(@NotNull Map<String, Component> map) {
        this.map = Collections.unmodifiableMap(map);
    }

    @Unmodifiable
    public Map<String, Component> asMap() {
        return map;
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public static AdventurePlaceholders empty() {
        return EMPTY;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<String, Component> entries = new LinkedHashMap<>();

        @Contract("_,_ -> this")
        public Builder with(@NotNull String key, @NotNull Component value) {
            this.entries.put(key, value);
            return this;
        }

        @Contract("_,_ -> this")
        public Builder with(@NotNull String key, @NotNull String value) {
            this.entries.put(key, Component.text(value));
            return this;
        }

        @Contract("_ -> this")
        public Builder with(@NotNull AdventurePlaceholders other) {
            this.entries.putAll(other.asMap());
            return this;
        }

        @Contract("_,_ -> this")
        public Builder with(@NotNull String key, @NotNull Object value) {
            this.entries.put(key, Component.text(String.valueOf(value)));
            return this;
        }

        public AdventurePlaceholders build() {
            if (this.entries.isEmpty()) {
                return EMPTY;
            }

            return new AdventurePlaceholders(new LinkedHashMap<>(this.entries));
        }
    }
}
