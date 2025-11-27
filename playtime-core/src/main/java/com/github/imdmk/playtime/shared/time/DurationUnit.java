package com.github.imdmk.playtime.shared.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Supported duration units and their metadata.
 * <p>
 * This enum centralizes singular/plural names, abbreviations, and extraction logic.
 */
public enum DurationUnit {

    DAY("day", "days", "d") {
        @Override
        int extract(@NotNull Duration duration) {
            return (int) duration.toDaysPart();
        }
    },
    HOUR("hour", "hours", "h") {
        @Override
        int extract(@NotNull Duration duration) {
            return duration.toHoursPart();
        }
    },
    MINUTE("minute", "minutes", "m") {
        @Override
        int extract(@NotNull Duration duration) {
            return duration.toMinutesPart();
        }
    },
    SECOND("second", "seconds", "s") {
        @Override
        int extract(@NotNull Duration duration) {
            return duration.toSecondsPart();
        }
    };

    /** Ordered for consistent output. */
    public static final DurationUnit[] ORDERED = {
            DAY, HOUR, MINUTE, SECOND
    };

    private static final String DISPLAY_NAME_FORMAT = "%d %s";

    private final String singular;
    private final String plural;
    private final String abbreviation;

    DurationUnit(@NotNull String singular, @NotNull String plural, @NotNull String abbreviation) {
        this.singular = singular;
        this.plural = plural;
        this.abbreviation = abbreviation;
    }

    abstract int extract(@NotNull Duration duration);

    public @NotNull String getAbbreviation() {
        return abbreviation;
    }

    public @NotNull String toDisplayName(int value) {
        String word = (value == 1 ? singular : plural);
        return DISPLAY_NAME_FORMAT.formatted(value, word);
    }
}
