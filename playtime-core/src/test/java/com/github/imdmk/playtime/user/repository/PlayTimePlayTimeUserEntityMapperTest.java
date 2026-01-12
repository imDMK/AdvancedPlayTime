package com.github.imdmk.playtime.user.repository;

import com.github.imdmk.playtime.feature.playtime.PlayTimeUserEntity;
import com.github.imdmk.playtime.feature.playtime.repository.UserEntityMapper;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.PlayTime;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayTimePlayTimeUserEntityMapperTest {

    private final UserEntityMapper mapper = new UserEntityMapper();

    @Test
    void toEntityShouldMapFieldsCorrectly() {
        var uuid = UUID.randomUUID();
        var user = new User(uuid, "DMK", PlayTime.ofMillis(3000));

        var entity = mapper.toEntity(user);

        assertThat(entity.getUuid()).isEqualTo(uuid);
        assertThat(entity.getName()).isEqualTo("DMK");
        assertThat(entity.getPlayTimeMillis()).isEqualTo(3000);
    }

    @Test
    void toDomainShouldMapFieldsCorrectly() {
        var uuid = UUID.randomUUID();
        var entity = new PlayTimeUserEntity(uuid, "XYZ", 5000);

        var user = mapper.toDomain(entity);

        assertThat(user.getUuid()).isEqualTo(uuid);
        assertThat(user.getName()).isEqualTo("XYZ");
        assertThat(user.getPlayTime().millis()).isEqualTo(5000);
    }

    @Test
    void toEntityShouldRejectNull() {
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void toDomainShouldRejectNull() {
        assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(NullPointerException.class);
    }
}

