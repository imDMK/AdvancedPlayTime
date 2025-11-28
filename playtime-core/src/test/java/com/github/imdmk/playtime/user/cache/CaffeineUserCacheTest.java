package com.github.imdmk.playtime.user.cache;

import com.github.imdmk.playtime.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CaffeineUserCacheTest {

    private CaffeineUserCache cache;

    @BeforeEach
    void setup() {
        cache = new CaffeineUserCache(
                Duration.ofHours(1),
                Duration.ofHours(1)
        );
    }

    private static User user(UUID uuid, String name) {
        return new User(uuid, name);
    }

    @Test
    void cacheUser_shouldStoreUserInBothIndexes() {
        var u = user(UUID.randomUUID(), "DMK");

        cache.cacheUser(u);

        assertThat(cache.getUserByUuid(u.getUuid()))
                .contains(u);

        assertThat(cache.getUserByName("DMK"))
                .contains(u);
    }

    @Test
    void cacheUser_shouldReplaceOldNameMappingOnNameChange() {
        var uuid = UUID.randomUUID();
        var oldUser = user(uuid, "DMK");
        var newUser = user(uuid, "DMKNew");

        cache.cacheUser(oldUser);
        cache.cacheUser(newUser);

        assertThat(cache.getUserByName("DMK")).isEmpty();
        assertThat(cache.getUserByName("DMKNew")).contains(newUser);
    }

    @Test
    void invalidateUser_shouldRemoveFromBothIndexes() {
        var u = user(UUID.randomUUID(), "Player1");
        cache.cacheUser(u);

        cache.invalidateUser(u);

        assertThat(cache.getUserByUuid(u.getUuid())).isEmpty();
        assertThat(cache.getUserByName("Player1")).isEmpty();
    }

    @Test
    void invalidateByUuid_shouldRemoveCorrectEntries() {
        var u = user(UUID.randomUUID(), "Nick");

        cache.cacheUser(u);
        cache.invalidateByUuid(u.getUuid());

        assertThat(cache.getUserByUuid(u.getUuid())).isEmpty();
        assertThat(cache.getUserByName("Nick")).isEmpty();
    }

    @Test
    void invalidateByName_shouldRemoveCorrectEntries() {
        var u = user(UUID.randomUUID(), "Tester");

        cache.cacheUser(u);
        cache.invalidateByName("Tester");

        assertThat(cache.getUserByUuid(u.getUuid())).isEmpty();
        assertThat(cache.getUserByName("Tester")).isEmpty();
    }

    @Test
    void getUserByUuid_shouldReturnEmptyWhenNotFound() {
        assertThat(cache.getUserByUuid(UUID.randomUUID())).isEmpty();
    }

    @Test
    void getUserByName_shouldReturnEmptyWhenNotFound() {
        assertThat(cache.getUserByName("Unknown")).isEmpty();
    }

    @Test
    void updateUserNameMapping_shouldUpdateNameIndexProperly() {
        var uuid = UUID.randomUUID();
        var oldUser = user(uuid, "A");
        var newUser = user(uuid, "B");

        cache.cacheUser(oldUser);
        cache.updateUserNameMapping(newUser, "A");

        assertThat(cache.getUserByName("A")).isEmpty();
        assertThat(cache.getUserByName("B")).contains(newUser);
    }

    @Test
    void forEachUser_shouldIterateAllUsers() {
        var u1 = user(UUID.randomUUID(), "One");
        var u2 = user(UUID.randomUUID(), "Two");

        cache.cacheUser(u1);
        cache.cacheUser(u2);

        AtomicInteger counter = new AtomicInteger();

        cache.forEachUser(u -> counter.incrementAndGet());

        assertThat(counter.get()).isEqualTo(2);
    }

    @Test
    void getCache_shouldReturnUnmodifiableSnapshot() {
        var u1 = user(UUID.randomUUID(), "One");
        cache.cacheUser(u1);

        var snapshot = cache.getCache();

        assertThat(snapshot).contains(u1);
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(snapshot::clear);
    }

    @Test
    void invalidateAll_shouldClearEverything() {
        var u1 = user(UUID.randomUUID(), "A");
        var u2 = user(UUID.randomUUID(), "B");

        cache.cacheUser(u1);
        cache.cacheUser(u2);

        cache.invalidateAll();

        assertThat(cache.getCache()).isEmpty();
        assertThat(cache.getUserByName("A")).isEmpty();
        assertThat(cache.getUserByName("B")).isEmpty();
    }
}

