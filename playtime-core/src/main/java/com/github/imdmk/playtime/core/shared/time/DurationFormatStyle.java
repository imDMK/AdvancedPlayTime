package com.github.imdmk.playtime.core.shared.time;

import java.time.Duration;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public enum DurationFormatStyle {

    COMPACT {
        @Override
        String format(Duration duration) {
            return formatWith(duration,
                    (unit, value) -> value + unit.getAbbreviation(),
                    Separator.SPACE);
        }
    },
    LONG {
        @Override
        String format(Duration duration) {
            return formatWith(duration,
                    DurationUnit::toDisplayName,
                    Separator.SPACE);
        }
    },
    LONG_WITH_AND {
        @Override
        String format(Duration duration) {
            return formatWith(duration,
                    DurationUnit::toDisplayName,
                    Separator.AND);
        }
    },
    NATURAL {
        @Override
        String format(Duration duration) {
            return formatWith(duration,
                    DurationUnit::toDisplayName,
                    Separator.COMMA);
        }
    };

    abstract String format(Duration duration);

    static String formatWith(
            Duration duration,
            BiFunction<DurationUnit, Integer, String> valueFormatter,
            Separator separator
    ) {
         Map<DurationUnit, Integer> parts = DurationSplitter.split(duration);
        return parts.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> valueFormatter.apply(e.getKey(), e.getValue()))
                .collect(Collectors.joining(separator.value()));
    }

    enum Separator {

        SPACE(" "),
        AND(" and "),
        COMMA(", ");

        private final String value;

        Separator(String value) {
            this.value = value;
        }

        String value() {
            return value;
        }
    }
}
