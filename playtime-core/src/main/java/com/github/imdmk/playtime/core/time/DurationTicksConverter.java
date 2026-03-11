package com.github.imdmk.playtime.core.time;

import java.time.Duration;

public final class DurationTicksConverter {

    private static final long TICKS_PER_MILLIS = 50;

    private DurationTicksConverter() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static long toTicks(Duration duration) {
        return duration.toMillis() / TICKS_PER_MILLIS;
    }
}
