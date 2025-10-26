package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.Validator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Immutable value object representing a time span in milliseconds.
 * <p>
 * Provides conversions to seconds, Bukkit ticks (1 tick = 50 ms),
 * and {@link Duration}, as well as basic arithmetic and comparison utilities.
 */
public record UserTime(long millis) implements Comparable<UserTime>, Serializable {

    @Serial private static final long serialVersionUID = 1L;

    public static final UserTime ZERO = new UserTime(0L);

    /** Milliseconds per Bukkit tick (50 ms). */
    private static final long MILLIS_PER_TICK = 50L;
    /** Milliseconds per second (1000 ms). */
    private static final long MILLIS_PER_SECOND = 1_000L;

    /**
     * Validates that the provided millisecond value is non-negative.
     *
     * @param millis time value in milliseconds
     * @throws IllegalArgumentException if {@code millis} is negative
     */
    public UserTime {
        if (millis < 0L) {
            throw new IllegalArgumentException("millis cannot be negative");
        }
    }

    /**
     * Creates a {@code UserTime} from the given milliseconds.
     *
     * @param millis time in milliseconds
     * @return new {@code UserTime} instance
     */
    @Contract("_ -> new")
    public static @NotNull UserTime ofMillis(long millis) {
        return new UserTime(millis);
    }

    /**
     * Creates a {@code UserTime} from seconds.
     *
     * @param seconds time in seconds
     * @return new {@code UserTime} instance
     */
    @Contract("_ -> new")
    public static @NotNull UserTime ofSeconds(long seconds) {
        return new UserTime(seconds * MILLIS_PER_SECOND);
    }

    /**
     * Creates a {@code UserTime} from Bukkit ticks.
     *
     * @param ticks number of ticks (1 tick = 50 ms)
     * @return new {@code UserTime} instance
     */
    @Contract("_ -> new")
    public static @NotNull UserTime ofTicks(long ticks) {
        return new UserTime(ticks * MILLIS_PER_TICK);
    }

    /**
     * Creates a {@code UserTime} from a {@link Duration}.
     *
     * @param duration non-null duration
     * @return new {@code UserTime} instance
     * @throws NullPointerException if {@code duration} is null
     */
    @Contract("_ -> new")
    public static @NotNull UserTime from(@NotNull Duration duration) {
        Validator.notNull(duration, "duration is null");
        return new UserTime(duration.toMillis());
    }

    /**
     * Converts this time to whole seconds (truncated).
     *
     * @return time in seconds
     */
    @Contract(pure = true)
    public long toSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(this.millis);
    }

    /**
     * Converts this time to Bukkit ticks (1 tick = 50 ms).
     *
     * @return time in ticks
     */
    @Contract(pure = true)
    public int toTicks() {
        return Math.toIntExact(this.millis / MILLIS_PER_TICK);
    }

    /**
     * Converts this time to a {@link Duration}.
     *
     * @return duration representation of this time
     */
    @Contract(pure = true)
    public @NotNull Duration toDuration() {
        return Duration.ofMillis(this.millis);
    }

    /**
     * Checks if the time equals zero.
     *
     * @return {@code true} if {@code millis == 0}
     */
    @Contract(pure = true)
    public boolean isZero() {
        return this.millis == 0L;
    }

    /**
     * Returns the sum of this and the given time.
     *
     * @param other time to add
     * @return new {@code UserTime} representing the sum
     * @throws ArithmeticException if overflow occurs
     */
    @Contract(pure = true)
    public @NotNull UserTime plus(@NotNull UserTime other) {
        Validator.notNull(other, "other UserTime is null");
        return new UserTime(Math.addExact(this.millis, other.millis));
    }

    /**
     * Returns the difference between this and the given time.
     *
     * @param other time to subtract
     * @return new {@code UserTime} representing the difference
     * @throws ArithmeticException if overflow occurs
     */
    @Contract(pure = true)
    public @NotNull UserTime minus(@NotNull UserTime other) {
        Validator.notNull(other, "other UserTime is null");
        return new UserTime(Math.subtractExact(this.millis, other.millis));
    }

    /**
     * Returns the smaller of this and the given time.
     *
     * @param other time to compare
     * @return smaller {@code UserTime}
     */
    @Contract(pure = true)
    public @NotNull UserTime min(@NotNull UserTime other) {
        Validator.notNull(other, "other UserTime is null");
        return this.millis <= other.millis ? this : other;
    }

    /**
     * Returns the larger of this and the given time.
     *
     * @param other time to compare
     * @return larger {@code UserTime}
     */
    @Contract(pure = true)
    public @NotNull UserTime max(@NotNull UserTime other) {
        Validator.notNull(other, "other UserTime is null");
        return this.millis >= other.millis ? this : other;
    }

    /**
     * Compares this {@code UserTime} with another by their millisecond values.
     *
     * @param o the other {@code UserTime}
     * @return negative if less, zero if equal, positive if greater
     */
    @Override
    public int compareTo(@NotNull UserTime o) {
        return Long.compare(this.millis, o.millis);
    }
}
