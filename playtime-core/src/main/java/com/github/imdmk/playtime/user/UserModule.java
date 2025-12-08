package com.github.imdmk.playtime.user;

import com.github.imdmk.playtime.infrastructure.module.Module;
import com.github.imdmk.playtime.infrastructure.module.phase.CommandPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.ListenerPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.RepositoryPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.TaskPhase;
import com.github.imdmk.playtime.user.cache.CaffeineUserCache;
import com.github.imdmk.playtime.user.cache.UserCache;
import com.github.imdmk.playtime.user.listener.UserJoinListener;
import com.github.imdmk.playtime.user.listener.UserQuitListener;
import com.github.imdmk.playtime.user.repository.UserEntityMapper;
import com.github.imdmk.playtime.user.repository.UserRepository;
import com.github.imdmk.playtime.user.repository.UserRepositoryOrmLite;
import com.github.imdmk.playtime.user.top.MemoryTopUsersCache;
import com.github.imdmk.playtime.user.top.TopUsersCache;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

public final class UserModule implements Module {

    private UserCache userCache;
    private UserEntityMapper userEntityMapper;
    private UserRepository userRepository;
    private TopUsersCache topUsersCache;
    private UserService userService;

    @Override
    public void bind(@NotNull Resources resources) {
        resources.on(UserCache.class).assignInstance(() -> this.userCache);
        resources.on(UserEntityMapper.class).assignInstance(() -> this.userEntityMapper);
        resources.on(UserRepository.class).assignInstance(() -> this.userRepository);
        resources.on(TopUsersCache.class).assignInstance(() -> this.topUsersCache);
        resources.on(UserService.class).assignInstance(() -> this.userService);
    }

    @Override
    public void init(@NotNull Injector injector) {
        this.userCache = new CaffeineUserCache();
        this.userEntityMapper = new UserEntityMapper();
        this.userRepository = injector.newInstance(UserRepositoryOrmLite.class);
        this.topUsersCache = injector.newInstance(MemoryTopUsersCache.class);
        this.userService = injector.newInstance(UserServiceImpl.class);
    }

    @Override
    public ListenerPhase listeners(@NotNull Injector injector) {
        return registrar -> registrar.register(
                injector.newInstance(UserJoinListener.class),
                injector.newInstance(UserQuitListener.class)
        );
    }

    @Override
    public RepositoryPhase repositories(@NotNull Injector injector) {
        return manager -> manager.register(this.userRepository);
    }

    @Override
    public CommandPhase commands(@NotNull Injector injector) {
        return builder -> builder.argument(User.class, injector.newInstance(UserArgument.class));
    }

    @Override
    public TaskPhase tasks(@NotNull Injector injector) {
        return scheduler -> {
            scheduler.runTimerAsync(injector.newInstance(UserSaveTask.class));
        };
    }
}
