package com.github.imdmk.playtime.shared.time;

import com.github.imdmk.playtime.shared.Validator;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

/**
 * Utility class responsible for splitting a {@link Duration}
 * into its component units (days, hours, minutes, seconds).
 * <p>
 * This keeps the extraction logic in a single place, shared across
 * different formatters.
 */
public final class DurationSplitter {

    private DurationSplitter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Splits the given duration into ordered units: days, hours, minutes, seconds.
     *
     * @param duration the duration to split (non-null)
     * @return map of {@link DurationUnit} to its value in the given duration
     */
    public static @NotNull Map<DurationUnit, Integer> split(@NotNull Duration duration) {
        Validator.notNull(duration, "duration cannot be null");

        EnumMap<DurationUnit, Integer> parts = new EnumMap<>(DurationUnit.class);
        for (DurationUnit unit : DurationUnit.ORDERED) {
            parts.put(unit, unit.extract(duration));
        }

        return parts;
    }
}

