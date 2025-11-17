package com.github.imdmk.spenttime.shared.time;

import com.github.imdmk.spenttime.shared.Validator;
import dev.rollczi.litecommands.time.DurationParser;
import dev.rollczi.litecommands.time.TemporalAmountParser;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for working with {@link Duration}: parsing, formatting,
 * and safe normalization (clamping) of time values.
 * <p>
 * This class is platform-agnostic and relies on LiteCommands'
 * {@link DurationParser} under the hood.
 */
public final class Durations {

    /**
     * Shared parser for duration strings using LiteCommands' {@link DurationParser}.
     * <p>
     * Supported suffixes:
     * <ul>
     *   <li>s – seconds</li>
     *   <li>m – minutes</li>
     *   <li>h – hours</li>
     *   <li>d – days</li>
     *   <li>w – weeks</li>
     *   <li>mo – months</li>
     *   <li>y – years</li>
     * </ul>
     */
    public static final TemporalAmountParser<Duration> PARSER = new DurationParser()
            .withUnit("s", ChronoUnit.SECONDS)
            .withUnit("m", ChronoUnit.MINUTES)
            .withUnit("h", ChronoUnit.HOURS)
            .withUnit("d", ChronoUnit.DAYS)
            .withUnit("w", ChronoUnit.WEEKS)
            .withUnit("mo", ChronoUnit.MONTHS)
            .withUnit("y", ChronoUnit.YEARS);

    /** Upper bound for any clamped duration (10 years). */
    private static final Duration MAX = Duration.ofDays(3650);

    /** Default placeholder for durations below one second. */
    private static final String LESS_THAN_SECOND_FORMAT = "<1s";

    private Durations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Formats a given duration to a human-readable string.
     * <p>
     * Returns {@code "<1s>"} if the duration is zero or negative.
     *
     * @param duration the duration to format (must not be null)
     * @return formatted string representation
     */
    public static @NotNull String format(@NotNull Duration duration) {
        Validator.notNull(duration, "duration cannot be null");

        if (duration.isZero() || duration.isNegative()) {
            return LESS_THAN_SECOND_FORMAT;
        }

        return PARSER.format(duration);
    }

    /**
     * Normalizes (clamps) the given duration so it’s always non-negative
     * and does not exceed {@link #MAX}.
     *
     * @param input duration to normalize (must not be null)
     * @return clamped, non-negative duration
     */
    public static @NotNull Duration clamp(@NotNull Duration input) {
        Validator.notNull(input, "duration cannot be null");

        if (input.isNegative()) {
            return Duration.ZERO;
        }

        return input.compareTo(MAX) > 0 ? MAX : input;
    }

    /** Alias for {@link #PARSER} getter, for DI or testing purposes. */
    public static @NotNull TemporalAmountParser<Duration> parser() {
        return PARSER;
    }
}
