package com.github.imdmk.playtime.user.repository;

import com.github.imdmk.playtime.infrastructure.database.repository.ormlite.EntityMapper;
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserTime;
import org.jetbrains.annotations.NotNull;

/**
 * Maps between the persistent {@link UserEntity} and the in-memory {@link User}.
 */
public final class UserEntityMapper implements EntityMapper<UserEntity, User> {

    @Override
    public @NotNull UserEntity toEntity(@NotNull User user) {
        Validator.notNull(user, "user cannot be null");
        return new UserEntity(
                user.getUuid(),
                user.getName(),
                user.getPlaytime().millis()
        );
    }

    @Override
    public @NotNull User toDomain(@NotNull UserEntity entity) {
        Validator.notNull(entity, "entity cannot be null");
        return new User(
                entity.getUuid(),
                entity.getName(),
                UserTime.ofMillis(entity.getPlaytime())
        );
    }
}
