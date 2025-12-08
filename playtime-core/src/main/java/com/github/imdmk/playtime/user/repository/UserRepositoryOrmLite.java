package com.github.imdmk.playtime.user.repository;

import com.github.imdmk.playtime.infrastructure.database.repository.RepositoryContext;
import com.github.imdmk.playtime.infrastructure.database.repository.ormlite.BaseDaoRepository;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.shared.validate.Validator;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserDeleteResult;
import com.github.imdmk.playtime.user.UserDeleteStatus;
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

    private final PluginLogger logger;
    private final UserEntityMapper mapper;

    @Inject
    public UserRepositoryOrmLite(
            @NotNull PluginLogger logger,
            @NotNull RepositoryContext context,
            @NotNull UserEntityMapper mapper
    ) {
        super(logger, context);
        this.logger = Validator.notNull(logger, "logger");
        this.mapper = Validator.notNull(mapper, "mapper");
    }

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
        Validator.notNull(uuid, "uuid");
        return executeAsync(() -> {
            try {
                return Optional.ofNullable(dao.queryForId(uuid))
                        .map(mapper::toDomain);
            } catch (SQLException e) {
                logger.error(e, "Failed to find user by uuid: %s", uuid);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<User>> findByName(@NotNull String name) {
        Validator.notNull(name, "name");
        return executeAsync(() -> {
            try {
                UserEntity entity = dao.queryBuilder()
                        .where().eq(UserEntityMeta.Col.NAME, name)
                        .queryForFirst();
                return Optional.ofNullable(entity).map(mapper::toDomain);
            } catch (SQLException e) {
                logger.error(e, "Failed to find user by name: %s", name);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<List<User>> findAll() {
        return executeAsync(() -> {
            try {
                return mapper.toDomainList(dao.queryForAll());
            } catch (SQLException e) {
                logger.error(e, "Failed to find all users");
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<List<User>> findTopByPlayTime(long limit) {
        if (limit <= 0) {
            return CompletableFuture.completedFuture(List.of());
        }

        return executeAsync(() -> {
            try {
                return mapper.toDomainList(
                        dao.queryBuilder()
                                .orderBy(UserEntityMeta.Col.PLAYTIME_MILLIS, false) // DESC
                                .orderBy(UserEntityMeta.Col.UUID, true)          // deterministic tiebreaker
                                .limit(limit)
                                .query()
                );
            } catch (SQLException e) {
                logger.error(e, "Failed to find top users (limit %s) by spent time", limit);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<User> save(@NotNull User user) {
        Validator.notNull(user, "user");
        return executeAsync(() -> {
            try {
                dao.createOrUpdate(mapper.toEntity(user));
                return user;
            } catch (SQLException e) {
                logger.error(e, "Failed to save user: %s", user.getUuid());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<UserDeleteResult> deleteByUuid(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid");
        return executeAsync(() -> {
            try {
                UserEntity userEntity = dao.queryForId(uuid);
                if (userEntity == null) {
                    return new UserDeleteResult(null, UserDeleteStatus.NOT_FOUND);
                }

                User user = mapper.toDomain(userEntity);

                int rows = dao.deleteById(uuid);
                return rows > 0
                        ? new UserDeleteResult(user, UserDeleteStatus.DELETED)
                        : new UserDeleteResult(user, UserDeleteStatus.FAILED);
            } catch (SQLException e) {
                logger.error(e, "Failed to delete user by uuid: %s", uuid);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<UserDeleteResult> deleteByName(@NotNull String name) {
        Validator.notNull(name, "name");
        return executeAsync(() -> {
            try {
                UserEntity userEntity = dao.queryBuilder()
                        .where().eq(UserEntityMeta.Col.NAME, name)
                        .queryForFirst();
                if (userEntity == null) {
                    return new UserDeleteResult(null, UserDeleteStatus.NOT_FOUND);
                }

                User user = mapper.toDomain(userEntity);

                int rows = dao.delete(userEntity);
                return rows > 0
                        ? new UserDeleteResult(user, UserDeleteStatus.DELETED)
                        : new UserDeleteResult(user, UserDeleteStatus.FAILED);
            } catch (SQLException e) {
                logger.error(e, "Failed to delete user by name: %s", name);
                throw new RuntimeException(e);
            }
        });
    }
}
