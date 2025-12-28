package com.github.imdmk.playtime.database.driver;

import com.github.imdmk.playtime.database.DatabaseMode;
import com.github.imdmk.playtime.database.driver.configurer.DriverConfigurerFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class DriverConfigurerFactoryTest {

    @Test
    void shouldReturnConfigurerForEachSupportedMode() {
        for (DatabaseMode mode : DatabaseMode.values()) {
            assertThatCode(() -> DriverConfigurerFactory.getFor(mode))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    void shouldRejectNullMode() {
        assertThatNullPointerException()
                .isThrownBy(() -> DriverConfigurerFactory.getFor(null));
    }

    @Test
    void shouldThrowForUnsupportedMode() {
        // Just to be sure — if enum expands in future
        // create invalid fake enum value
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> DriverConfigurerFactory.getFor(DatabaseMode.valueOf("NON_EXISTENT"))); // if ever added
    }
}

