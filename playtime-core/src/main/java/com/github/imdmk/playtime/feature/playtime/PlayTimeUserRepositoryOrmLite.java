package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.database.DatabaseBootstrap;
import com.github.imdmk.playtime.database.repository.ormlite.OrmLiteRepository;
import com.github.imdmk.playtime.injector.annotations.Repository;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
final class PlayTimeUserRepositoryOrmLite
        extends OrmLiteRepository<PlayTimeUserEntity, UUID>
        implements PlayTimeUserRepository {

    private final PlayTimeUserEntityMapper mapper;

    @Inject
    PlayTimeUserRepositoryOrmLite(
            @NotNull PluginLogger logger,
            @NotNull TaskScheduler taskScheduler,
            @NotNull DatabaseBootstrap databaseBootstrap
    ) {
        super(logger, taskScheduler, databaseBootstrap);
        this.mapper = new PlayTimeUserEntityMapper();
    }

    @Override
    protected Class<PlayTimeUserEntity> entityClass() {
        return PlayTimeUserEntity.class;
    }

    @Override
    public CompletableFuture<Optional<PlayTimeUser>> findByUuid(@NotNull UUID uuid) {
        return execute(() -> {
            try {
                return Optional.ofNullable(dao.queryForId(uuid))
                        .map(mapper::toDomain);
            } catch (SQLException e) {
                logger.error(e, "Failed to find user with uuid %s", uuid);
                throw new IllegalStateException("Database failure", e);
            }
        });
    }

    @Override
    public CompletableFuture<List<PlayTimeUser>> findAll() {
        return execute(() -> {
            try {
                return dao.queryForAll().stream()
                        .map(mapper::toDomain)
                        .toList();
            } catch (SQLException e) {
                logger.error(e, "Failed to query all users");
                throw new IllegalStateException("Database failure", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteByUuid(@NotNull UUID uuid) {
        return execute(() -> {
            try {
                final int rows = dao.deleteById(uuid);
                return rows > 0;
            } catch (SQLException e) {
                logger.error(e, "Failed to delete user with uuid %s", uuid);
                throw new IllegalStateException("Database failure", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> save(@NotNull PlayTimeUser user) {
        execute(() -> {
            try {
                dao.createOrUpdate(mapper.toEntity(user));
                return null;
            } catch (SQLException e) {
                logger.error(e, "Failed to save user with uuid %s", user.getUuid());
                throw new IllegalStateException("Database failure", e);
            }
        });
    }
}
