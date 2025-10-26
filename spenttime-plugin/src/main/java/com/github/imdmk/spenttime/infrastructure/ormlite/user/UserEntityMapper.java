package com.github.imdmk.spenttime.infrastructure.ormlite.user;

import com.github.imdmk.spenttime.infrastructure.ormlite.EntityMapper;
import com.github.imdmk.spenttime.Validator;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserTime;
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
                user.getSpentTime().millis()
        );
    }

    @Override
    public @NotNull User toDomain(@NotNull UserEntity entity) {
        Validator.notNull(entity, "entity cannot be null");
        return new User(
                entity.getUuid(),
                entity.getName(),
                UserTime.ofMillis(entity.getSpentMillis())
        );
    }
}
