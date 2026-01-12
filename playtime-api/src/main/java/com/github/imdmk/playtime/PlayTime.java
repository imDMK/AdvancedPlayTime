package com.github.imdmk.playtime;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public record PlayTime(long millis) implements Comparable<PlayTime> {

    private static final long MILLIS_PER_TICK = 50L;

    public static final PlayTime ZERO = new PlayTime(0L);

    public PlayTime {
        if (millis < 0L) {
            throw new IllegalArgumentException("PlayTime millis cannot be negative");
        }
    }

    public static PlayTime of(@NotNull Duration duration) {
        return new PlayTime(duration.toMillis());
    }

    public static PlayTime ofMillis(long millis) {
        return new PlayTime(millis);
    }

    public static PlayTime ofTicks(long ticks) {
        return new PlayTime(Math.multiplyExact(ticks, MILLIS_PER_TICK));
    }

    public long toMillis() {
        return millis;
    }

    public Duration toDuration() {
        return Duration.ofMillis(millis);
    }

    public long toSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(millis);
    }

    public int toTicks() {
        return Math.toIntExact(millis / MILLIS_PER_TICK);
    }

    public boolean isZero() {
        return millis == 0;
    }

    public PlayTime plus(@NotNull PlayTime other) {
        return new PlayTime(Math.addExact(millis, other.millis));
    }

    public PlayTime minus(@NotNull PlayTime other) {
        return new PlayTime(Math.subtractExact(millis, other.millis));
    }

    @Override
    public int compareTo(PlayTime o) {
        return Long.compare(millis, o.millis);
    }
}
