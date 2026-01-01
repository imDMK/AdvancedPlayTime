package com.github.imdmk.playtime.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public enum DurationUnit {

    DAY("day", "days", "d") {
        @Override
        public int extract(@NotNull Duration duration) {
            return (int) duration.toDaysPart();
        }
    },
    HOUR("hour", "hours", "h") {
        @Override
        public int extract(@NotNull Duration duration) {
            return duration.toHoursPart();
        }
    },
    MINUTE("minute", "minutes", "m") {
        @Override
        public int extract(@NotNull Duration duration) {
            return duration.toMinutesPart();
        }
    },
    SECOND("second", "seconds", "s") {
        @Override
        public int extract(@NotNull Duration duration) {
            return duration.toSecondsPart();
        }
    };

    protected static final DurationUnit[] ORDERED = {
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

    public abstract int extract(@NotNull Duration duration);

    public String getAbbreviation() {
        return abbreviation;
    }

    protected String toDisplayName(int value) {
        final String word = (value == 1 ? singular : plural);
        return DISPLAY_NAME_FORMAT.formatted(value, word);
    }
}
