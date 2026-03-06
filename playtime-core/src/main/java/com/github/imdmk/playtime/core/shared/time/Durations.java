package com.github.imdmk.playtime.core.shared.time;

import java.time.Duration;

public final class Durations {

    private static final Duration MAX_NORMALIZED_DURATION = Duration.ofDays(3650);
    private static final String LESS_THAN_SECOND = "<1s";
    private static final long MILLIS_PER_TICK = 50L;

    private static DurationFormatStyle FORMAT_STYLE = DurationFormatStyle.NATURAL;

    private Durations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String format(Duration duration, DurationFormatStyle style) {
        if (duration.isZero() || duration.isNegative()) {
            return LESS_THAN_SECOND;
        }

        return style.format(duration);
    }

    public static String format(Duration duration) {
        return format(duration, FORMAT_STYLE);
    }

    public static Duration clamp(Duration input) {
        if (input.isNegative()) {
            return Duration.ZERO;
        }

        return input.compareTo(MAX_NORMALIZED_DURATION) > 0 ? MAX_NORMALIZED_DURATION : input;
    }

    public static int convertToTicks(Duration duration) {
        long ticks = duration.toMillis() / MILLIS_PER_TICK;
        return ticks <= 0 ? 0 : (int) ticks;
    }

    public static void setFormatStyle(DurationFormatStyle style) {
        FORMAT_STYLE = style;
    }
}
