package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.UserTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class UserTimeTest {

    @Nested
    @DisplayName("Construction & factories")
    class ConstructionTests {

        @Test
        void shouldCreateWithMillis() {
            UserTime t = UserTime.ofMillis(1500);
            assertThat(t.millis()).isEqualTo(1500);
        }

        @Test
        void shouldRejectNegativeMillis() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new UserTime(-1))
                    .withMessageContaining("negative");
        }

        @Test
        void shouldCreateFromSeconds() {
            UserTime t = UserTime.ofSeconds(2);
            assertThat(t.millis()).isEqualTo(2000);
        }

        @Test
        void shouldCreateFromTicks() {
            UserTime t = UserTime.ofTicks(10);
            assertThat(t.millis()).isEqualTo(10 * 50);
        }

        @Test
        void shouldCreateFromDuration() {
            Duration d = Duration.ofMillis(1234);
            UserTime t = UserTime.from(d);
            assertThat(t.millis()).isEqualTo(1234);
        }

        @Test
        void shouldRejectNullDuration() {
            assertThatNullPointerException()
                    .isThrownBy(() -> UserTime.from(null))
                    .withMessageContaining("duration");
        }
    }

    @Nested
    @DisplayName("Conversions")
    class ConversionTests {

        @Test
        void shouldConvertToSeconds() {
            UserTime t = UserTime.ofMillis(2500);
            assertThat(t.toSeconds()).isEqualTo(2);
        }

        @Test
        void shouldConvertToTicks() {
            UserTime t = UserTime.ofMillis(250);
            assertThat(t.toTicks()).isEqualTo(5); // 250 / 50
        }

        @Test
        void shouldConvertToDuration() {
            UserTime t = UserTime.ofMillis(500);
            assertThat(t.toDuration()).isEqualTo(Duration.ofMillis(500));
        }
    }

    @Nested
    @DisplayName("Arithmetic")
    class ArithmeticTests {

        @Test
        void shouldAddUserTimes() {
            UserTime a = UserTime.ofMillis(1000);
            UserTime b = UserTime.ofMillis(2000);
            assertThat(a.plus(b)).isEqualTo(UserTime.ofMillis(3000));
        }

        @Test
        void shouldThrowOnAddOverflow() {
            UserTime a = UserTime.ofMillis(Long.MAX_VALUE);
            UserTime b = UserTime.ofMillis(1);

            assertThatExceptionOfType(ArithmeticException.class)
                    .isThrownBy(() -> a.plus(b));
        }

        @Test
        void shouldSubtractUserTimes() {
            UserTime a = UserTime.ofMillis(3000);
            UserTime b = UserTime.ofMillis(1000);
            assertThat(a.minus(b)).isEqualTo(UserTime.ofMillis(2000));
        }

        @Test
        void shouldRejectNullInPlus() {
            UserTime t = UserTime.ZERO;

            assertThatNullPointerException()
                    .isThrownBy(() -> t.plus(null));
        }

        @Test
        void shouldRejectNullInMinus() {
            UserTime t = UserTime.ZERO;

            assertThatNullPointerException()
                    .isThrownBy(() -> t.minus(null));
        }
    }

    @Nested
    @DisplayName("Min/Max")
    class MinMaxTests {

        @Test
        void shouldReturnMin() {
            UserTime a = UserTime.ofMillis(500);
            UserTime b = UserTime.ofMillis(1000);

            assertThat(a.min(b)).isEqualTo(a);
            assertThat(b.min(a)).isEqualTo(a);
        }

        @Test
        void shouldReturnMax() {
            UserTime a = UserTime.ofMillis(500);
            UserTime b = UserTime.ofMillis(1000);

            assertThat(a.max(b)).isEqualTo(b);
            assertThat(b.max(a)).isEqualTo(b);
        }

        @Test
        void shouldRejectNullInMin() {
            UserTime a = UserTime.ZERO;

            assertThatNullPointerException()
                    .isThrownBy(() -> a.min(null));
        }

        @Test
        void shouldRejectNullInMax() {
            UserTime a = UserTime.ZERO;

            assertThatNullPointerException()
                    .isThrownBy(() -> a.max(null));
        }
    }

    @Nested
    @DisplayName("Comparison")
    class ComparisonTests {

        @Test
        void shouldCompareByMillis() {
            UserTime a = UserTime.ofMillis(100);
            UserTime b = UserTime.ofMillis(200);
            UserTime c = UserTime.ofMillis(100);

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
            UserTime a = UserTime.ofMillis(500);
            UserTime b = UserTime.ofMillis(500);

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        void shouldNotBeEqualWhenMillisDiffers() {
            UserTime a = UserTime.ofMillis(500);
            UserTime b = UserTime.ofMillis(300);

            assertThat(a).isNotEqualTo(b);
        }
    }

    @Test
    void shouldIdentifyZero() {
        assertThat(UserTime.ZERO.isZero()).isTrue();
        assertThat(UserTime.ofMillis(1).isZero()).isFalse();
    }

    @Test
    void toStringShouldContainMillis() {
        UserTime t = UserTime.ofMillis(1234);
        assertThat(t.toString()).contains("millis=1234");
    }
}

