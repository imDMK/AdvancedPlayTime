package com.github.imdmk.playtime.core.feature.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.database.repository.ormlite.EntityMapper;


final class PlayTimeUserEntityMapper
        implements EntityMapper<PlayTimeUserEntity, PlayTimeUser> {

    @Override
    public PlayTimeUserEntity toEntity(PlayTimeUser user) {
        return new PlayTimeUserEntity(
                user.getUuid(),
                user.getPlayTime().millis()
        );
    }

    @Override
    public PlayTimeUser toDomain(PlayTimeUserEntity entity) {
        return new PlayTimeUser(
                entity.getUuid(),
                PlayTime.ofMillis(entity.getPlayTimeMillis())
        );
    }
}
