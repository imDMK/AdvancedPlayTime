package com.github.imdmk.playtime.shared.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class DurationsTest {

    @BeforeEach
    void resetDefaultStyle() {
        Durations.setDefaultFormatStyle(DurationFormatStyle.NATURAL);
    }

    @Test
    void format_zeroOrNegative_shouldReturnLessThanOneSecond() {
        assertThat(Durations.format(Duration.ZERO)).isEqualTo("<1s");
        assertThat(Durations.format(Duration.ofMillis(-500))).isEqualTo("<1s");
    }

    @Test
    void format_withDefaultStyleShouldUseNatural() {
        String result = Durations.format(Duration.ofSeconds(65));
        assertThat(result).isEqualTo("1 minute, 5 seconds");
    }

    @Test
    void format_withCustomStyleShouldFormatProperly() {
        String result = Durations.format(Duration.ofSeconds(3661), DurationFormatStyle.COMPACT);
        assertThat(result).isEqualTo("1h 1m 1s");
    }

    @Test
    void setDefaultFormatStyle_shouldAffectGlobalFormatting() {
        Durations.setDefaultFormatStyle(DurationFormatStyle.COMPACT);
        String result = Durations.format(Duration.ofMinutes(3));

        assertThat(result).isEqualTo("3m");
    }

    @Test
    void clamp_negative_shouldReturnZero() {
        assertThat(Durations.clamp(Duration.ofMillis(-1)))
                .isEqualTo(Duration.ZERO);
    }

    @Test
    void clamp_aboveMax_shouldClampToMax() {
        Duration tenYears = Duration.ofDays(5000);
        Duration clamped = Durations.clamp(tenYears);

        assertThat(clamped).isEqualTo(Duration.ofDays(3650));
    }

    @Test
    void clamp_withinRange_shouldReturnSame() {
        Duration d = Duration.ofHours(12);
        assertThat(Durations.clamp(d)).isEqualTo(d);
    }
}

