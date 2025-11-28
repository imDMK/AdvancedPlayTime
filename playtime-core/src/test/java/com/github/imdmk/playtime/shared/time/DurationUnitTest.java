package com.github.imdmk.playtime.shared.time;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class DurationUnitTest {

    @Test
    void extract_shouldReturnCorrectValues() {
        Duration d = Duration.ofDays(1)
                .plusHours(2)
                .plusMinutes(3)
                .plusSeconds(4);

        assertThat(DurationUnit.DAY.extract(d)).isEqualTo(1);
        assertThat(DurationUnit.HOUR.extract(d)).isEqualTo(2);
        assertThat(DurationUnit.MINUTE.extract(d)).isEqualTo(3);
        assertThat(DurationUnit.SECOND.extract(d)).isEqualTo(4);
    }

    @Test
    void toDisplayName_shouldUseSingularOrPluralProperly() {
        assertThat(DurationUnit.HOUR.toDisplayName(1)).isEqualTo("1 hour");
        assertThat(DurationUnit.HOUR.toDisplayName(5)).isEqualTo("5 hours");
    }

    @Test
    void getAbbreviation_shouldReturnCorrectStrings() {
        assertThat(DurationUnit.DAY.getAbbreviation()).isEqualTo("d");
        assertThat(DurationUnit.SECOND.getAbbreviation()).isEqualTo("s");
    }
}

