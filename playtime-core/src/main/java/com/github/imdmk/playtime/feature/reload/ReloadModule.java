package com.github.imdmk.playtime.feature.reload;

import com.github.imdmk.playtime.infrastructure.module.Module;
import com.github.imdmk.playtime.infrastructure.module.phase.CommandPhase;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

public final class ReloadModule implements Module {

    @Override
    public void bind(@NotNull Resources resources) {}

    @Override
    public void init(@NotNull Injector injector) {}

    @Override
    public CommandPhase commands(@NotNull Injector injector) {
        return configurer -> configurer.registerCommands(injector.newInstance(ReloadCommand.class));
    }
}
