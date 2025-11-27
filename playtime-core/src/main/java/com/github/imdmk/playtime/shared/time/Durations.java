package com.github.imdmk.playtime.shared.time;

import com.github.imdmk.playtime.shared.Validator;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Utility class providing human-readable formatting helpers for {@link Duration}.
 * <p>
 * Supports multiple predefined {@link DurationFormatStyle} strategies.
 * Zero or negative durations are normalized to {@code "<1s"}.
 * <p>
 * This class is stateless apart from the configurable default style.
 */
public final class Durations {

    /** Upper bound for any clamped duration (10 years). */
    private static final Duration MAX_NORMALIZED_DURATION = Duration.ofDays(3650);

    /** Returned when the duration is zero or negative. */
    private static final String LESS_THAN_SECOND = "<1s";

    /** Default style used when no explicit format style is provided. */
    private static DurationFormatStyle DEFAULT_FORMAT_STYLE = DurationFormatStyle.NATURAL;

    private Durations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Formats the given duration using {@link #DEFAULT_FORMAT_STYLE}.
     * <p>
     * Zero or negative durations return {@code "<1s"}.
     *
     * @param duration the duration to format (non-null)
     * @return formatted duration string (never {@code null})
     */
    public static @NotNull String format(@NotNull Duration duration) {
        return format(duration, DEFAULT_FORMAT_STYLE);
    }

    /**
     * Formats the given duration using the specified {@link DurationFormatStyle}.
     * <p>
     * Zero or negative durations return {@code "<1s"}.
     *
     * @param duration the duration to format (non-null)
     * @param style    formatting strategy (non-null)
     * @return human-readable duration string (never {@code null})
     * @throws IllegalArgumentException if duration or style are {@code null}
     */
    public static @NotNull String format(@NotNull Duration duration, @NotNull DurationFormatStyle style) {
        Validator.notNull(duration, "duration cannot be null");
        Validator.notNull(style, "style cannot be null");

        if (duration.isZero() || duration.isNegative()) {
            return LESS_THAN_SECOND;
        }

        return style.format(duration);
    }

    /**
     * Sets the global default {@link DurationFormatStyle} used by
     * {@link #format(Duration)}.
     * <p>
     * This modifies process-wide behavior and should be configured during
     * plugin initialization.
     *
     * @param style the new default style (non-null)
     * @throws IllegalArgumentException if the provided style is {@code null}
     */
    public static void setDefaultFormatStyle(@NotNull DurationFormatStyle style) {
        Validator.notNull(style, "durationFormatStyle cannot be null");
        DEFAULT_FORMAT_STYLE = style;
    }

    /**
     * Normalizes (clamps) the given duration so it’s always non-negative
     * and does not exceed {@link #MAX_NORMALIZED_DURATION}.
     *
     * @param input duration to normalize (must not be null)
     * @return clamped, non-negative duration
     */
    public static @NotNull Duration clamp(@NotNull Duration input) {
        Validator.notNull(input, "duration cannot be null");

        if (input.isNegative()) {
            return Duration.ZERO;
        }

        return input.compareTo(MAX_NORMALIZED_DURATION) > 0 ? MAX_NORMALIZED_DURATION : input;
    }
}
