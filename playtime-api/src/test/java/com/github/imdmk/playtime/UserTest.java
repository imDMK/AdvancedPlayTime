package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class UserTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        void shouldInitializeFieldsCorrectly() {
            UUID uuid = UUID.randomUUID();
            PlayTime time = PlayTime.ofMillis(5000);

            User user = new User(uuid, "Player", time);

            assertThat(user.getUuid()).isEqualTo(uuid);
            assertThat(user.getName()).isEqualTo("Player");
            assertThat(user.getPlayTime()).isEqualTo(time);
        }

        @Test
        void shouldCreateUserWithZeroPlayTime() {
            User user = new User(UUID.randomUUID(), "Player");

            assertThat(user.getPlayTime()).isEqualTo(PlayTime.ZERO);
        }

        @Test
        void shouldThrowWhenNameIsNull() {
            UUID uuid = UUID.randomUUID();

            assertThatNullPointerException()
                    .isThrownBy(() -> new User(uuid, null, PlayTime.ZERO))
                    .withMessageContaining("name");
        }

        @Test
        void shouldThrowWhenPlayTimeIsNull() {
            UUID uuid = UUID.randomUUID();

            assertThatNullPointerException()
                    .isThrownBy(() -> new User(uuid, "Player", null))
                    .withMessageContaining("playtime");
        }
    }

    @Nested
    @DisplayName("Name mutation")
    class NameTests {

        @Test
        void shouldUpdateName() {
            User user = new User(UUID.randomUUID(), "Old");

            user.setName("New");

            assertThat(user.getName()).isEqualTo("New");
        }

        @Test
        void shouldRejectNullName() {
            User user = new User(UUID.randomUUID(), "Old");

            assertThatNullPointerException()
                    .isThrownBy(() -> user.setName(null))
                    .withMessageContaining("name");
        }

        @Test
        void shouldRejectBlankName() {
            User user = new User(UUID.randomUUID(), "Old");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> user.setName("   "))
                    .withMessageContaining("blank");
        }
    }

    @Nested
    @DisplayName("PlayTime mutation")
    class PlayTimeTests {

        @Test
        void shouldReturnCurrentPlayTimeAsUserTime() {
            User user = new User(UUID.randomUUID(), "Player", PlayTime.ofMillis(1000));

            assertThat(user.getPlayTime().millis()).isEqualTo(1000);
        }

        @Test
        void shouldSetNewPlayTime() {
            User user = new User(UUID.randomUUID(), "Player");

            user.setPlayTime(PlayTime.ofMillis(12345));

            assertThat(user.getPlayTime().millis()).isEqualTo(12345);
        }

        @Test
        void shouldRejectNullPlayTime() {
            User user = new User(UUID.randomUUID(), "Player");

            assertThatNullPointerException()
                    .isThrownBy(() -> user.setPlayTime(null))
                    .withMessageContaining("playtime");
        }
    }

    @Nested
    @DisplayName("Equality & Hashcode")
    class EqualityTests {

        @Test
        void usersWithSameUuidShouldBeEqual() {
            UUID uuid = UUID.randomUUID();

            User u1 = new User(uuid, "A");
            User u2 = new User(uuid, "B");

            assertThat(u1).isEqualTo(u2);
            assertThat(u1).hasSameHashCodeAs(u2);
        }

        @Test
        void usersWithDifferentUuidShouldNotBeEqual() {
            User u1 = new User(UUID.randomUUID(), "A");
            User u2 = new User(UUID.randomUUID(), "A");

            assertThat(u1).isNotEqualTo(u2);
        }
    }

    @Test
    void toStringShouldContainKeyInformation() {
        User user = new User(UUID.randomUUID(), "Player", PlayTime.ofMillis(100));

        String str = user.toString();

        assertThat(str)
                .contains("uuid=")
                .contains("name='Player'")
                .contains("playtimeMillis=100");
    }
}
