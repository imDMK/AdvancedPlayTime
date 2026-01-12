package com.github.imdmk.playtime.user.repository;

import com.github.imdmk.playtime.feature.playtime.PlayTimeUserEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayTimeUserEntityTest {

    @Test
    void constructorShouldSetFields() {
        var uuid = UUID.randomUUID();
        var entity = new PlayTimeUserEntity(uuid, "DMK", 12345);

        assertThat(entity.getUuid()).isEqualTo(uuid);
        assertThat(entity.getName()).isEqualTo("DMK");
        assertThat(entity.getPlayTimeMillis()).isEqualTo(12345);
    }

    @Test
    void settersShouldUpdateFields() {
        var entity = new PlayTimeUserEntity();

        var uuid = UUID.randomUUID();
        entity.setUuid(uuid);
        entity.setName("DMK");
        entity.setPlayTimeMillis(500);

        assertThat(entity.getUuid()).isEqualTo(uuid);
        assertThat(entity.getName()).isEqualTo("DMK");
        assertThat(entity.getPlayTimeMillis()).isEqualTo(500);
    }

    @Test
    void equalsShouldCompareByUuidOnly() {
        var uuid = UUID.randomUUID();

        var a = new PlayTimeUserEntity(uuid, "A", 1);
        var b = new PlayTimeUserEntity(uuid, "B", 9999);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentUuid() {
        var a = new PlayTimeUserEntity(UUID.randomUUID(), "A", 1);
        var b = new PlayTimeUserEntity(UUID.randomUUID(), "A", 1);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toStringShouldContainFields() {
        var uuid = UUID.randomUUID();
        var e = new PlayTimeUserEntity(uuid, "X", 123);

        var s = e.toString();

        assertThat(s).contains("uuid=" + uuid);
        assertThat(s).contains("name='X'");
        assertThat(s).contains("spentMillis=123");
    }
}

