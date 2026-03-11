package com.github.imdmk.playtime.core.feature.playtime.repository;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.database.repository.ormlite.EntityMapper;
import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;

final class PlayTimeUserEntityMapper
        implements EntityMapper<PlayTimeUserEntity, PlayTimeUser> {

    @Override
    public PlayTimeUserEntity toEntity(PlayTimeUser user) {
        return new PlayTimeUserEntity(
                user.getUuid(),
                user.getName(),
                user.getPlayTime().millis()
        );
    }

    @Override
    public PlayTimeUser toDomain(PlayTimeUserEntity entity) {
        return new PlayTimeUser(
                entity.getUuid(),
                entity.getName(),
                PlayTime.ofMillis(entity.getPlayTimeMillis())
        );
    }
}
