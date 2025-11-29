package com.github.imdmk.playtime.shared.time;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class DurationFormatStyleTest {

    @Test
    void compact_formatShouldUseAbbreviations() {
        String out = DurationFormatStyle.COMPACT.format(Duration.ofSeconds(3665));
        assertThat(out).isEqualTo("1h 1m 5s");
    }

    @Test
    void long_formatShouldUseFullNames() {
        String out = DurationFormatStyle.LONG.format(Duration.ofMinutes(61));
        assertThat(out).isEqualTo("1 hour 1 minute");
    }

    @Test
    void longWithAnd_formatShouldUseAndSeparator() {
        String out = DurationFormatStyle.LONG_WITH_AND.format(Duration.ofSeconds(62));
        assertThat(out).isEqualTo("1 minute and 2 seconds");
    }

    @Test
    void natural_formatShouldUseCommaSeparator() {
        String out = DurationFormatStyle.NATURAL.format(Duration.ofHours(24 + 2));
        assertThat(out).isEqualTo("1 day, 2 hours");
    }

    @Test
    void formatWith_noNonZeroUnitsShouldReturnEmpty() {
        String out = DurationFormatStyle.NATURAL.format(Duration.ZERO);
        assertThat(out).isEmpty();
    }
}

