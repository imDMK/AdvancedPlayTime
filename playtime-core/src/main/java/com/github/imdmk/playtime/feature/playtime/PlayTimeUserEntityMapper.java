package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.database.repository.ormlite.EntityMapper;
import org.jetbrains.annotations.NotNull;

final class PlayTimeUserEntityMapper
        implements EntityMapper<PlayTimeUserEntity, PlayTimeUser> {

    @Override
    public PlayTimeUserEntity toEntity(@NotNull PlayTimeUser user) {
        return new PlayTimeUserEntity(
                user.getUuid(),
                user.getPlayTime().millis()
        );
    }

    @Override
    public PlayTimeUser toDomain(@NotNull PlayTimeUserEntity entity) {
        return new PlayTimeUser(
                entity.getUuid(),
                PlayTime.ofMillis(entity.getPlayTimeMillis())
        );
    }
}
