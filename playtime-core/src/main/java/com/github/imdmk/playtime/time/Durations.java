package com.github.imdmk.playtime.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class Durations {

    private static final Duration MAX_NORMALIZED_DURATION = Duration.ofDays(3650);
    private static final String LESS_THAN_SECOND = "<1s";
    private static DurationFormatStyle DEFAULT_FORMAT_STYLE = DurationFormatStyle.NATURAL;

    private Durations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String format(@NotNull Duration duration) {
        return format(duration, DEFAULT_FORMAT_STYLE);
    }

    public static String format(@NotNull Duration duration, @NotNull DurationFormatStyle style) {
        if (duration.isZero() || duration.isNegative()) {
            return LESS_THAN_SECOND;
        }

        return style.format(duration);
    }

    public static void setDefaultFormatStyle(@NotNull DurationFormatStyle style) {
        DEFAULT_FORMAT_STYLE = style;
    }

    public static Duration clamp(@NotNull Duration input) {
        if (input.isNegative()) {
            return Duration.ZERO;
        }

        return input.compareTo(MAX_NORMALIZED_DURATION) > 0 ? MAX_NORMALIZED_DURATION : input;
    }
}
