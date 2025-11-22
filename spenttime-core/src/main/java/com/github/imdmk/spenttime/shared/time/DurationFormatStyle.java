package com.github.imdmk.spenttime.shared.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Defines formatting strategies for converting a {@link Duration} into
 * a human-readable string.
 * <p>
 * Each style provides its own implementation of {@link #format(Duration)}.
 * The underlying logic splits the duration into days, hours, minutes and seconds
 * and then renders only non-zero units in a style-specific way.
 */
public enum DurationFormatStyle {

    /**
     * Compact representation using short unit abbreviations.
     * <p>
     * Example: {@code 30d 30m 3s}
     */
    COMPACT {
        @Override
        public String format(@NotNull Duration duration) {
            return formatWith(duration,
                    (unit, value) -> value + unit.getAbbreviation(),
                    Separator.SPACE);
        }
    },

    /**
     * Long form with full unit names, separated by spaces.
     * <p>
     * Example: {@code 30 days 30 minutes 3 seconds}
     */
    LONG {
        @Override
        public String format(@NotNull Duration duration) {
            return formatWith(duration,
                    DurationUnit::toDisplayName,
                    Separator.SPACE);
        }
    },

    /**
     * Long form with {@code " and "} between units.
     * <p>
     * Example: {@code 30 days and 30 minutes and 3 seconds}
     */
    LONG_WITH_AND {
        @Override
        public String format(@NotNull Duration duration) {
            return formatWith(duration,
                    DurationUnit::toDisplayName,
                    Separator.AND);
        }
    },

    /**
     * Natural language-like form using commas between units.
     * <p>
     * Example: {@code 30 days, 30 minutes, 3 seconds}
     */
    NATURAL {
        @Override
        public String format(@NotNull Duration duration) {
            return formatWith(duration,
                    DurationUnit::toDisplayName,
                    Separator.COMMA);
        }
    };

    /**
     * Formats the given {@link Duration} using this style.
     * <p>
     * The duration is first decomposed into days, hours, minutes and seconds,
     * and only non-zero units are included in the output.
     *
     * @param duration the duration to format; must not be {@code null}
     * @return formatted duration according to this style (never {@code null})
     */
    public abstract String format(@NotNull Duration duration);

    /**
     * Joins non-zero units of the given duration using the provided formatter
     * and separator.
     *
     * @param duration       duration to format
     * @param valueFormatter function converting (unit, value) → string
     * @param separator      separator strategy
     * @return formatted string, or empty string if all units are zero
     */
    protected static String formatWith(
            @NotNull Duration duration,
            @NotNull BiFunction<DurationUnit, Integer, String> valueFormatter,
            @NotNull Separator separator
    ) {
        Map<DurationUnit, Integer> parts = DurationSplitter.split(duration);

        return parts.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> valueFormatter.apply(e.getKey(), e.getValue()))
                .collect(Collectors.joining(separator.value()));
    }

    /**
     * Separator strategies used between formatted units.
     */
    protected enum Separator {

        SPACE(" "),
        AND(" and "),
        COMMA(", ");

        private final String value;

        Separator(@NotNull String value) {
            this.value = value;
        }

        /**
         * Returns the underlying separator string.
         *
         * @return separator value
         */
        @NotNull
        public String value() {
            return value;
        }
    }
}
