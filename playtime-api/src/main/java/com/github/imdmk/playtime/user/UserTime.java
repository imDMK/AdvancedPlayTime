package com.github.imdmk.playtime.user;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public record UserTime(long millis) implements Comparable<UserTime>, Serializable {

    @Serial private static final long serialVersionUID = 1L;

    public static final UserTime ZERO = new UserTime(0L);

    private static final long MILLIS_PER_TICK = 50L;
    private static final long MILLIS_PER_SECOND = 1_000L;

    public UserTime {
        if (millis < 0L) {
            throw new IllegalArgumentException("UserTime millis cannot be negative");
        }
    }

    @Contract("_ -> new")
    public static @NotNull UserTime ofMillis(long millis) {
        return new UserTime(millis);
    }

    @Contract("_ -> new")
    public static @NotNull UserTime ofDuration(@NotNull Duration duration) {
        return ofMillis(duration.toMillis());
    }

    @Contract("_ -> new")
    public static @NotNull UserTime ofSeconds(long seconds) {
        return new UserTime(seconds * MILLIS_PER_SECOND);
    }

    @Contract("_ -> new")
    public static @NotNull UserTime ofTicks(long ticks) {
        return new UserTime(ticks * MILLIS_PER_TICK);
    }

    @Contract("_ -> new")
    public static @NotNull UserTime from(@NotNull Duration duration) {
        return new UserTime(duration.toMillis());
    }

    @Contract(pure = true)
    public long toSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(millis);
    }

    @Contract(pure = true)
    public int toTicks() {
        return Math.toIntExact(millis / MILLIS_PER_TICK);
    }

    @Contract(pure = true)
    public @NotNull Duration toDuration() {
        return Duration.ofMillis(millis);
    }

    @Contract(pure = true)
    public boolean isZero() {
        return millis == 0;
    }

    @Contract(pure = true)
    public UserTime plus(@NotNull UserTime other) {
        return new UserTime(Math.addExact(millis, other.millis));
    }

    @Contract(pure = true)
    public UserTime minus(@NotNull UserTime other) {
        return new UserTime(Math.subtractExact(millis, other.millis));
    }

    @Contract(pure = true)
    public UserTime min(@NotNull UserTime other) {
        return millis <= other.millis ? this : other;
    }

    @Contract(pure = true)
    public UserTime max(@NotNull UserTime other) {
        return millis >= other.millis ? this : other;
    }

    @Override
    public int compareTo(@NotNull UserTime o) {
        return Long.compare(millis, o.millis);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserTime userTime = (UserTime) o;
        return compareTo(userTime) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(millis);
    }

    @Override
    @NotNull
    public String toString() {
        return "UserTime{" +
                "millis=" + millis +
                '}';
    }
}
