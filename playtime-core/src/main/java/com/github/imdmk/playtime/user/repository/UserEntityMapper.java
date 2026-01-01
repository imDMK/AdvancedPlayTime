package com.github.imdmk.playtime.user.repository;

import com.github.imdmk.playtime.database.repository.ormlite.EntityMapper;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.priority.Priority;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserTime;
import org.jetbrains.annotations.NotNull;

@Service(priority = Priority.LOW)
public final class UserEntityMapper
        implements EntityMapper<UserEntity, User> {

    @Override
    public UserEntity toEntity(@NotNull User user) {
        return new UserEntity(
                user.getUuid(),
                user.getName(),
                user.getPlaytime().millis()
        );
    }

    @Override
    public User toDomain(@NotNull UserEntity entity) {
        return new User(
                entity.getUuid(),
                entity.getName(),
                UserTime.ofMillis(entity.getPlaytimeMillis())
        );
    }
}
