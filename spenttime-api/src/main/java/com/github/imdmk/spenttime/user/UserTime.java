package com.github.imdmk.spenttime.user;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Immutable value object representing a duration of time measured in milliseconds.
 *
 * <p>This record provides convenient conversions between milliseconds, seconds,
 * Bukkit ticks (1 tick = 50 ms), and {@link Duration}, as well as arithmetic
 * and comparison utilities for working with user playtime or uptime data.</p>
 *
 * <p><strong>Design notes:</strong></p>
 * <ul>
 *   <li>This class enforces non-negative values — negative durations are not allowed.</li>
 *   <li>All operations return new immutable instances; this class is thread-safe and
 *       safe for concurrent use.</li>
 *   <li>Overflow conditions in arithmetic methods trigger {@link ArithmeticException}
 *       to prevent silent wrap-around.</li>
 * </ul>
 *
 * Serves as the canonical representation of player time in the SpentTime module.
 *
 * @param millis the milliseconds of time
 *
 * @see Duration
 * @see User
 */
public record UserTime(long millis) implements Comparable<UserTime>, Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** Constant representing zero time. */
    public static final UserTime ZERO = new UserTime(0L);

    /** Number of milliseconds in a single Bukkit tick (20 ticks = 1 second). */
    private static final long MILLIS_PER_TICK = 50L;

    /** Number of milliseconds in one second. */
    private static final long MILLIS_PER_SECOND = 1_000L;

    /**
     * Primary constructor that validates the provided time value.
     *
     * @param millis total duration in milliseconds (must be ≥ 0)
     * @throws IllegalArgumentException if {@code millis} is negative
     */
    public UserTime {
        if (millis < 0L) {
            throw new IllegalArgumentException("UserTime millis cannot be negative");
        }
    }

    /**
     * Creates a {@code UserTime} from raw milliseconds.
     *
     * @param millis total milliseconds
     * @return a new {@code UserTime} instance
     */
    @Contract("_ -> new")
    public static @NotNull UserTime ofMillis(long millis) {
        return new UserTime(millis);
    }

    /**
     * Creates a {@code UserTime} from duration.
     *
     * @param duration a duration
     * @return a new {@code UserTime} instance
     */
    @Contract("_ -> new")
    public static @NotNull UserTime ofDuration(@NotNull Duration duration) {
        Objects.requireNonNull(duration, "duration cannot be null");
        return ofMillis(duration.toMillis());
    }

    /**
     * Creates a {@code UserTime} from seconds.
     *
     * @param seconds total seconds
     * @return a new {@code UserTime} instance
     */
    @Contract("_ -> new")
    public static @NotNull UserTime ofSeconds(long seconds) {
        return new UserTime(seconds * MILLIS_PER_SECOND);
    }

    /**
     * Creates a {@code UserTime} from Bukkit ticks (1 tick = 50 ms).
     *
     * @param ticks total ticks
     * @return a new {@code UserTime} instance
     */
    @Contract("_ -> new")
    public static @NotNull UserTime ofTicks(long ticks) {
        return new UserTime(ticks * MILLIS_PER_TICK);
    }

    /**
     * Creates a {@code UserTime} from a {@link Duration}.
     *
     * @param duration non-null duration to convert
     * @return a new {@code UserTime} instance
     * @throws NullPointerException if {@code duration} is null
     */
    @Contract("_ -> new")
    public static @NotNull UserTime from(@NotNull Duration duration) {
        Objects.requireNonNull(duration, "duration is null");
        return new UserTime(duration.toMillis());
    }

    /**
     * Converts this time to whole seconds (truncated).
     *
     * @return number of seconds contained in this duration
     */
    @Contract(pure = true)
    public long toSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(this.millis);
    }

    /**
     * Converts this time to Bukkit ticks (1 tick = 50 ms).
     *
     * @return total number of ticks represented by this time
     */
    @Contract(pure = true)
    public int toTicks() {
        return Math.toIntExact(this.millis / MILLIS_PER_TICK);
    }

    /**
     * Converts this instance to a {@link Duration}.
     *
     * @return a duration representing the same amount of time
     */
    @Contract(pure = true)
    public @NotNull Duration toDuration() {
        return Duration.ofMillis(this.millis);
    }

    /**
     * Returns whether this time equals zero.
     *
     * @return {@code true} if this duration represents zero milliseconds
     */
    @Contract(pure = true)
    public boolean isZero() {
        return this.millis == 0L;
    }

    /**
     * Adds another {@code UserTime} to this one.
     *
     * @param other non-null {@code UserTime} to add
     * @return new {@code UserTime} representing the sum
     * @throws NullPointerException if {@code other} is null
     * @throws ArithmeticException if overflow occurs
     */
    @Contract(pure = true)
    public @NotNull UserTime plus(@NotNull UserTime other) {
        Objects.requireNonNull(other, "other UserTime is null");
        return new UserTime(Math.addExact(this.millis, other.millis));
    }

    /**
     * Subtracts another {@code UserTime} from this one.
     *
     * @param other non-null {@code UserTime} to subtract
     * @return new {@code UserTime} representing the difference
     * @throws NullPointerException if {@code other} is null
     * @throws ArithmeticException if overflow occurs
     */
    @Contract(pure = true)
    public @NotNull UserTime minus(@NotNull UserTime other) {
        Objects.requireNonNull(other, "other UserTime is null");
        return new UserTime(Math.subtractExact(this.millis, other.millis));
    }

    /**
     * Returns the smaller of this and the given time.
     *
     * @param other non-null time to compare
     * @return the smaller {@code UserTime} instance
     */
    @Contract(pure = true)
    public @NotNull UserTime min(@NotNull UserTime other) {
        Objects.requireNonNull(other, "other UserTime is null");
        return this.millis <= other.millis ? this : other;
    }

    /**
     * Returns the larger of this and the given time.
     *
     * @param other non-null time to compare
     * @return the larger {@code UserTime} instance
     */
    @Contract(pure = true)
    public @NotNull UserTime max(@NotNull UserTime other) {
        Objects.requireNonNull(other, "other UserTime is null");
        return this.millis >= other.millis ? this : other;
    }

    /**
     * Compares this {@code UserTime} with another by their millisecond values.
     *
     * @param o other {@code UserTime} to compare against
     * @return negative if this is less, zero if equal, positive if greater
     */
    @Override
    public int compareTo(@NotNull UserTime o) {
        return Long.compare(this.millis, o.millis);
    }

    /**
     * Checks equality based on millisecond value.
     *
     * @param o object to compare
     * @return {@code true} if the given object is a {@code UserTime} with the same millisecond value
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserTime userTime = (UserTime) o;
        return compareTo(userTime) == 0;
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash based on the millisecond value
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(millis);
    }

    /**
     * Returns a concise string representation suitable for logging or debugging.
     *
     * @return string in the format {@code "UserTime{millis=X}"}
     */
    @Override
    @NotNull
    public String toString() {
        return "UserTime{" +
                "millis=" + millis +
                '}';
    }
}
