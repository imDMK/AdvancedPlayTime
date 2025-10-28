package com.github.imdmk.spenttime.shared.time;

import com.github.imdmk.spenttime.shared.Validator;
import dev.rollczi.litecommands.time.DurationParser;
import dev.rollczi.litecommands.time.TemporalAmountParser;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for working with {@link Duration}, including parsing,
 * formatting, and conversion to Minecraft ticks.
 * <p>
 * This class is platform-agnostic and uses LiteCommands' {@link DurationParser}
 * under the hood.
 */
public final class Durations {

    /**
     * Shared parser for duration strings using LiteCommands' {@link DurationParser}.
     * Supports suffixes: s, m, h, d, w, mo, y
     */
    public static final TemporalAmountParser<Duration> PARSER = new DurationParser()
            .withUnit("s", ChronoUnit.SECONDS)
            .withUnit("m", ChronoUnit.MINUTES)
            .withUnit("h", ChronoUnit.HOURS)
            .withUnit("d", ChronoUnit.DAYS)
            .withUnit("w", ChronoUnit.WEEKS)
            .withUnit("mo", ChronoUnit.MONTHS)
            .withUnit("y", ChronoUnit.YEARS);

    /** Default format returned when duration is zero or negative. */
    private static final String LESS_THAN_SECOND = "<1s";

    private Durations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Formats the given duration into a readable string using {@link #PARSER}.
     *
     * @param duration the duration to format (non-null)
     * @return the formatted string, or "&lt;1s" if duration is zero or negative
     */
    public static String format(@NotNull Duration duration) {
        Validator.notNull(duration, "duration cannot be null");

        if (duration.isZero() || duration.isNegative()) {
            return LESS_THAN_SECOND;
        }

        return PARSER.format(duration);
    }

    public @NotNull TemporalAmountParser<Duration> getDurationParser() {
        return PARSER;
    }
}

