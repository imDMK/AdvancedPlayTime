package com.github.imdmk.playtime.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

public final class DurationSplitter {

    private DurationSplitter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static Map<DurationUnit, Integer> split(@NotNull Duration duration) {
        final EnumMap<DurationUnit, Integer> parts = new EnumMap<>(DurationUnit.class);
        for (final DurationUnit unit : DurationUnit.ORDERED) {
            parts.put(unit, unit.extract(duration));
        }

        return parts;
    }
}

