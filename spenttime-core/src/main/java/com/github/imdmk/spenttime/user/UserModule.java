package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.infrastructure.module.PluginModule;
import com.github.imdmk.spenttime.infrastructure.module.phase.CommandPhase;
import com.github.imdmk.spenttime.infrastructure.module.phase.ListenerPhase;
import com.github.imdmk.spenttime.infrastructure.module.phase.RepositoryPhase;
import com.github.imdmk.spenttime.user.cache.CaffeineUserCache;
import com.github.imdmk.spenttime.user.cache.UserCache;
import com.github.imdmk.spenttime.user.listener.UserJoinListener;
import com.github.imdmk.spenttime.user.listener.UserQuitListener;
import com.github.imdmk.spenttime.user.repository.UserEntityMapper;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.user.repository.UserRepositoryOrmLite;
import com.github.imdmk.spenttime.user.top.MemoryTopUsersCache;
import com.github.imdmk.spenttime.user.top.TopUsersCache;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

public final class UserModule implements PluginModule {

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
        return configurer -> configurer.configure(builder -> {
            builder.argument(User.class, injector.newInstance(UserArgument.class));
        });
    }
}
