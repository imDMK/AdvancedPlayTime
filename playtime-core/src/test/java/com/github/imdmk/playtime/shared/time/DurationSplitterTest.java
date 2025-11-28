package com.github.imdmk.playtime.shared.time;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DurationSplitterTest {

    @Test
    void split_shouldReturnCorrectParts() {
        Duration d = Duration.ofDays(2)
                .plusHours(5)
                .plusMinutes(30)
                .plusSeconds(10);

        Map<DurationUnit, Integer> parts = DurationSplitter.split(d);

        assertThat(parts.get(DurationUnit.DAY)).isEqualTo(2);
        assertThat(parts.get(DurationUnit.HOUR)).isEqualTo(5);
        assertThat(parts.get(DurationUnit.MINUTE)).isEqualTo(30);
        assertThat(parts.get(DurationUnit.SECOND)).isEqualTo(10);
    }

    @Test
    void split_zeroDurationShouldReturnAllZero() {
        Map<DurationUnit, Integer> parts = DurationSplitter.split(Duration.ZERO);

        assertThat(parts.values()).allMatch(v -> v == 0);
    }
}

