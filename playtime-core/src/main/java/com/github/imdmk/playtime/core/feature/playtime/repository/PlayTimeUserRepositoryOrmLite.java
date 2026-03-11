package com.github.imdmk.playtime.core.feature.playtime.repository;

import com.github.imdmk.playtime.core.database.DatabaseBootstrap;
import com.github.imdmk.playtime.core.database.repository.ormlite.OrmLiteRepository;
import com.github.imdmk.playtime.core.feature.playtime.PlayTimeUser;
import com.github.imdmk.playtime.core.injector.annotations.Repository;
import com.github.imdmk.playtime.core.platform.logger.PluginLogger;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
final class PlayTimeUserRepositoryOrmLite
        extends OrmLiteRepository<PlayTimeUserEntity, UUID>
        implements PlayTimeUserRepository {

    private static final PlayTimeUserEntityMapper MAPPER = new PlayTimeUserEntityMapper();

    @Inject
    PlayTimeUserRepositoryOrmLite(
            PluginLogger logger,
            TaskScheduler taskScheduler,
            DatabaseBootstrap databaseBootstrap
    ) {
        super(logger, taskScheduler, databaseBootstrap);
    }

    @Override
    protected Class<PlayTimeUserEntity> entityClass() {
        return PlayTimeUserEntity.class;
    }

    @Override
    public CompletableFuture<PlayTimeUser> findByUuid(UUID uuid) {
        return execute(() -> {
            try {
                PlayTimeUserEntity entity = dao.queryForId(uuid);
                if (entity == null) {
                    return null;
                }

                return MAPPER.toDomain(entity);
            } catch (SQLException e) {
                logger.error(e, "Failed to find user with uuid %s", uuid);
                throw new IllegalStateException("Database failure", e);
            }
        });
    }

    @Override
    public CompletableFuture<PlayTimeUser> findByName(String name) {
        return execute(() -> {
            try {
                PlayTimeUserEntity entity = dao.queryForFirst(dao.queryBuilder()
                        .where()
                        .eq(PlayTimeUserEntityMeta.Col.NAME, name)
                        .prepare()
                );
                if (entity == null) {
                    return null;
                }

                return MAPPER.toDomain(entity);
            } catch (SQLException e) {
                logger.error(e, "Failed to find user with name %s", name);
                throw new IllegalStateException("Database failure", e);
            }
        });
    }

    @Override
    public CompletableFuture<List<PlayTimeUser>> findTopByPlayTime(int limit) {
        if (limit <= 0) {
            return CompletableFuture.completedFuture(List.of());
        }

        return execute(() -> {
            try {
                return dao.queryBuilder()
                        .orderBy(PlayTimeUserEntityMeta.Col.PLAYTIME_MILLIS, false)
                        .limit((long) limit)
                        .query()
                        .stream()
                        .map(MAPPER::toDomain)
                        .toList();
            } catch (SQLException e) {
                logger.error(e, "Failed to query top playtime users (limit=%d)", limit);
                throw new IllegalStateException("Database failure", e);
            }
        });
    }


    @Override
    public CompletableFuture<List<PlayTimeUser>> findAll() {
        return execute(() -> {
            try {
                return dao.queryForAll().stream()
                        .map(MAPPER::toDomain)
                        .toList();
            } catch (SQLException e) {
                logger.error(e, "Failed to query all users");
                throw new IllegalStateException("Database failure", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteByUuid(UUID uuid) {
        return execute(() -> {
            try {
                int rows = dao.deleteById(uuid);
                return rows > 0;
            } catch (SQLException e) {
                logger.error(e, "Failed to delete user with uuid %s", uuid);
                throw new IllegalStateException("Database failure", e);
            }
        });
    }

    @Override
    public CompletableFuture<PlayTimeUser> save(PlayTimeUser user) {
        return execute(() -> {
            try {
                dao.createOrUpdate(MAPPER.toEntity(user));
                return user;
            } catch (SQLException e) {
                logger.error(e, "Failed to save user with uuid %s", user.getUuid());
                throw new IllegalStateException("Database failure", e);
            }
        });
    }
}
