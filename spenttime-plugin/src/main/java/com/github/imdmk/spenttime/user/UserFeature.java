package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.infrastructure.module.PluginModuleFeature;
import com.github.imdmk.spenttime.platform.litecommands.LiteCommandsConfigurer;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

import java.util.function.Consumer;

public class UserFeature implements PluginModuleFeature {

    @Override
    public void bind(@NotNull Resources resources) {}

    @Override
    public void init(@NotNull Injector injector) {}

    @Override
    public Consumer<LiteCommandsConfigurer> commands(@NotNull Injector injector) {
        return configurer -> configurer.configure(builder -> {
            builder.argument(User.class, injector.newInstanceWithFields(UserArgument.class));
        });
    }

    @Override
    public int order() {
        return 10;
    }
}
