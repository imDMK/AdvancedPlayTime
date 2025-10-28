package com.github.imdmk.spenttime.infrastructure.database.ormlite.user;

import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.infrastructure.database.ormlite.BaseDaoRepository;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserDeleteResult;
import com.github.imdmk.spenttime.user.UserDeleteStatus;
import com.github.imdmk.spenttime.user.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class UserRepositoryOrmLite
        extends BaseDaoRepository<UserEntity, UUID>
        implements UserRepository {

    @Inject private UserEntityMapper mapper;

    @Override
    protected Class<UserEntity> entityClass() {
        return UserEntity.class;
    }

    @Override
    protected List<Class<?>> entitySubClasses() {
        return List.of();
    }

    @Override
    public @NotNull CompletableFuture<Optional<User>> findByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");
        return executeAsync(() -> {
            try {
                return Optional.ofNullable(this.dao.queryForId(uuid))
                        .map(this.mapper::toDomain);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<User>> findByName(@NotNull String name) {
        Validator.notNull(name, "name cannot be null");
        return executeAsync(() -> {
            try {
                UserEntity entity = this.dao.queryBuilder()
                        .where().eq("name", name)
                        .queryForFirst();
                return Optional.ofNullable(entity).map(this.mapper::toDomain);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<List<User>> findTopBySpentTime(long limit) {
        if (limit <= 0) {
            return CompletableFuture.completedFuture(List.of());
        }

        return executeAsync(() -> {
            try {
                return this.mapper.toDomainList(
                        this.dao.queryBuilder()
                                .orderBy("spentMillis", false) // DESC
                                .orderBy("uuid", true)         // deterministic tiebreaker
                                .limit(limit)
                                .query()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<User> save(@NotNull User user) {
        Validator.notNull(user, "user cannot be null");
        return executeAsync(() -> {
            try {
                this.dao.createOrUpdate(this.mapper.toEntity(user));
                return user;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<UserDeleteResult> deleteByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");
        return executeAsync(() -> {
            try {
                UserEntity userEntity = this.dao.queryForId(uuid);
                if (userEntity == null) {
                    return new UserDeleteResult(null, UserDeleteStatus.NOT_FOUND);
                }

                User user = this.mapper.toDomain(userEntity);

                int rows = this.dao.deleteById(uuid);
                return rows > 0
                        ? new UserDeleteResult(user, UserDeleteStatus.DELETED)
                        : new UserDeleteResult(user, UserDeleteStatus.FAILED);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<UserDeleteResult> deleteByName(@NotNull String name) {
        Validator.notNull(name, "name cannot be null");
        return executeAsync(() -> {
            try {
                UserEntity userEntity = this.dao.queryBuilder()
                        .where().eq("name", name)
                        .queryForFirst();
                if (userEntity == null) {
                    return new UserDeleteResult(null, UserDeleteStatus.NOT_FOUND);
                }

                User user = this.mapper.toDomain(userEntity);

                int rows = this.dao.delete(userEntity);
                return rows > 0
                        ? new UserDeleteResult(user, UserDeleteStatus.DELETED)
                        : new UserDeleteResult(user, UserDeleteStatus.FAILED);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
