package com.github.imdmk.spenttime.feature.reload;

import com.github.imdmk.spenttime.infrastructure.module.PluginModule;
import com.github.imdmk.spenttime.infrastructure.module.phase.CommandPhase;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

public class ReloadModule implements PluginModule {

    @Override
    public void bind(@NotNull Resources resources) {}

    @Override
    public void init(@NotNull Injector injector) {}

    @Override
    public CommandPhase commands(@NotNull Injector injector) {
        return configurer -> configurer.registerCommands(injector.newInstance(ReloadCommand.class));
    }
}
