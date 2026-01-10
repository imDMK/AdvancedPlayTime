package com.github.imdmk.playtime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class PlayTimeTest {

    @Nested
    @DisplayName("Construction & factories")
    class ConstructionTests {

        @Test
        void shouldCreateWithMillis() {
            PlayTime t = PlayTime.ofMillis(1500);
            assertThat(t.millis()).isEqualTo(1500);
        }

        @Test
        void shouldRejectNegativeMillis() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new PlayTime(-1))
                    .withMessageContaining("negative");
        }

        @Test
        void shouldCreateFromSeconds() {
            PlayTime t = PlayTime.ofSeconds(2);
            assertThat(t.millis()).isEqualTo(2000);
        }

        @Test
        void shouldCreateFromTicks() {
            PlayTime t = PlayTime.ofTicks(10);
            assertThat(t.millis()).isEqualTo(10 * 50);
        }

        @Test
        void shouldCreateFromDuration() {
            Duration d = Duration.ofMillis(1234);
            PlayTime t = PlayTime.from(d);
            assertThat(t.millis()).isEqualTo(1234);
        }

        @Test
        void shouldRejectNullDuration() {
            assertThatNullPointerException()
                    .isThrownBy(() -> PlayTime.from(null))
                    .withMessageContaining("duration");
        }
    }

    @Nested
    @DisplayName("Conversions")
    class ConversionTests {

        @Test
        void shouldConvertToSeconds() {
            PlayTime t = PlayTime.ofMillis(2500);
            assertThat(t.toSeconds()).isEqualTo(2);
        }

        @Test
        void shouldConvertToTicks() {
            PlayTime t = PlayTime.ofMillis(250);
            assertThat(t.toTicks()).isEqualTo(5); // 250 / 50
        }

        @Test
        void shouldConvertToDuration() {
            PlayTime t = PlayTime.ofMillis(500);
            assertThat(t.toDuration()).isEqualTo(Duration.ofMillis(500));
        }
    }

    @Nested
    @DisplayName("Arithmetic")
    class ArithmeticTests {

        @Test
        void shouldAddUserTimes() {
            PlayTime a = PlayTime.ofMillis(1000);
            PlayTime b = PlayTime.ofMillis(2000);
            assertThat(a.plus(b)).isEqualTo(PlayTime.ofMillis(3000));
        }

        @Test
        void shouldThrowOnAddOverflow() {
            PlayTime a = PlayTime.ofMillis(Long.MAX_VALUE);
            PlayTime b = PlayTime.ofMillis(1);

            assertThatExceptionOfType(ArithmeticException.class)
                    .isThrownBy(() -> a.plus(b));
        }

        @Test
        void shouldSubtractUserTimes() {
            PlayTime a = PlayTime.ofMillis(3000);
            PlayTime b = PlayTime.ofMillis(1000);
            assertThat(a.minus(b)).isEqualTo(PlayTime.ofMillis(2000));
        }

        @Test
        void shouldRejectNullInPlus() {
            PlayTime t = PlayTime.ZERO;

            assertThatNullPointerException()
                    .isThrownBy(() -> t.plus(null));
        }

        @Test
        void shouldRejectNullInMinus() {
            PlayTime t = PlayTime.ZERO;

            assertThatNullPointerException()
                    .isThrownBy(() -> t.minus(null));
        }
    }

    @Nested
    @DisplayName("Min/Max")
    class MinMaxTests {

        @Test
        void shouldReturnMin() {
            PlayTime a = PlayTime.ofMillis(500);
            PlayTime b = PlayTime.ofMillis(1000);

            assertThat(a.min(b)).isEqualTo(a);
            assertThat(b.min(a)).isEqualTo(a);
        }

        @Test
        void shouldReturnMax() {
            PlayTime a = PlayTime.ofMillis(500);
            PlayTime b = PlayTime.ofMillis(1000);

            assertThat(a.max(b)).isEqualTo(b);
            assertThat(b.max(a)).isEqualTo(b);
        }

        @Test
        void shouldRejectNullInMin() {
            PlayTime a = PlayTime.ZERO;

            assertThatNullPointerException()
                    .isThrownBy(() -> a.min(null));
        }

        @Test
        void shouldRejectNullInMax() {
            PlayTime a = PlayTime.ZERO;

            assertThatNullPointerException()
                    .isThrownBy(() -> a.max(null));
        }
    }

    @Nested
    @DisplayName("Comparison")
    class ComparisonTests {

        @Test
        void shouldCompareByMillis() {
            PlayTime a = PlayTime.ofMillis(100);
            PlayTime b = PlayTime.ofMillis(200);
            PlayTime c = PlayTime.ofMillis(100);

            assertThat(a.compareTo(b)).isNegative();
            assertThat(b.compareTo(a)).isPositive();
            assertThat(a.compareTo(c)).isZero();
        }
    }

    @Nested
    @DisplayName("Equality & Hashcode")
    class EqualityTests {

        @Test
        void shouldBeEqualWhenMillisIsSame() {
            PlayTime a = PlayTime.ofMillis(500);
            PlayTime b = PlayTime.ofMillis(500);

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        void shouldNotBeEqualWhenMillisDiffers() {
            PlayTime a = PlayTime.ofMillis(500);
            PlayTime b = PlayTime.ofMillis(300);

            assertThat(a).isNotEqualTo(b);
        }
    }

    @Test
    void shouldIdentifyZero() {
        assertThat(PlayTime.ZERO.isZero()).isTrue();
        assertThat(PlayTime.ofMillis(1).isZero()).isFalse();
    }

    @Test
    void toStringShouldContainMillis() {
        PlayTime t = PlayTime.ofMillis(1234);
        assertThat(t.toString()).contains("millis=1234");
    }
}

