package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.infrastructure.database.ormlite.user.UserEntityMapper;
import com.github.imdmk.spenttime.infrastructure.database.ormlite.user.UserRepositoryOrmLite;
import com.github.imdmk.spenttime.infrastructure.module.PluginModule;
import com.github.imdmk.spenttime.infrastructure.module.phase.CommandPhase;
import com.github.imdmk.spenttime.infrastructure.module.phase.RepositoryPhase;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

public class UserModule implements PluginModule {

    private UserCache userCache;

    private UserEntityMapper userEntityMapper;
    private UserRepository userRepository;

    private UserService userService;

    @Override
    public void bind(@NotNull Resources resources) {
        resources.on(UserCache.class).assignInstance(() -> this.userCache);
        resources.on(UserEntityMapper.class).assignInstance(() -> this.userEntityMapper);
        resources.on(UserRepository.class).assignInstance(() -> this.userRepository);
        resources.on(UserService.class).assignInstance(() -> this.userService);
    }

    @Override
    public void init(@NotNull Injector injector) {
        this.userCache = new UserCache();
        this.userEntityMapper = new UserEntityMapper();
        this.userRepository = injector.newInstanceWithFields(UserRepositoryOrmLite.class);
        this.userService = injector.newInstanceWithFields(UserServiceImpl.class);
    }

    @Override
    public RepositoryPhase repositories(@NotNull Injector injector) {
        return manager -> manager.register(this.userRepository);
    }

    @Override
    public CommandPhase commands(@NotNull Injector injector) {
        return configurer -> configurer.configure(builder -> {
            builder.argument(User.class, injector.newInstanceWithFields(UserArgument.class));
        });
    }
}
