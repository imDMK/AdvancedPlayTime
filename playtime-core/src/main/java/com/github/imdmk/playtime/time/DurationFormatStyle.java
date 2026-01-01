package com.github.imdmk.playtime.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public enum DurationFormatStyle {

    COMPACT {
        @Override
        public String format(@NotNull Duration duration) {
            return formatWith(duration,
                    (unit, value) -> value + unit.getAbbreviation(),
                    Separator.SPACE);
        }
    },
    LONG {
        @Override
        public String format(@NotNull Duration duration) {
            return formatWith(duration,
                    DurationUnit::toDisplayName,
                    Separator.SPACE);
        }
    },
    LONG_WITH_AND {
        @Override
        public String format(@NotNull Duration duration) {
            return formatWith(duration,
                    DurationUnit::toDisplayName,
                    Separator.AND);
        }
    },
    NATURAL {
        @Override
        public String format(@NotNull Duration duration) {
            return formatWith(duration,
                    DurationUnit::toDisplayName,
                    Separator.COMMA);
        }
    };

    public abstract String format(@NotNull Duration duration);

    protected static String formatWith(
            @NotNull Duration duration,
            @NotNull BiFunction<DurationUnit, Integer, String> valueFormatter,
            @NotNull Separator separator
    ) {
        final Map<DurationUnit, Integer> parts = DurationSplitter.split(duration);
        return parts.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> valueFormatter.apply(e.getKey(), e.getValue()))
                .collect(Collectors.joining(separator.value()));
    }

    protected enum Separator {

        SPACE(" "),
        AND(" and "),
        COMMA(", ");

        private final String value;

        Separator(@NotNull String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
