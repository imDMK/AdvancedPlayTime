package com.github.imdmk.playtime.core.shared.time;



import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

final class DurationSplitter {

    DurationSplitter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    static Map<DurationUnit, Integer> split(Duration duration) {
         EnumMap<DurationUnit, Integer> parts = new EnumMap<>(DurationUnit.class);
        for (DurationUnit unit : DurationUnit.ORDERED) {
            parts.put(unit, unit.extract(duration));
        }

        return parts;
    }
}

