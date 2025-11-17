package com.github.imdmk.spenttime.feature.playtime;

import com.github.imdmk.spenttime.PlaytimeService;
import com.github.imdmk.spenttime.feature.playtime.command.TimeCommand;
import com.github.imdmk.spenttime.feature.playtime.command.TimeSetCommand;
import com.github.imdmk.spenttime.feature.playtime.command.TimeTopCommand;
import com.github.imdmk.spenttime.feature.playtime.gui.PlaytimeTopGui;
import com.github.imdmk.spenttime.feature.playtime.listener.PlaytimeSaveListener;
import com.github.imdmk.spenttime.infrastructure.module.PluginModule;
import com.github.imdmk.spenttime.infrastructure.module.phase.CommandPhase;
import com.github.imdmk.spenttime.infrastructure.module.phase.GuiPhase;
import com.github.imdmk.spenttime.infrastructure.module.phase.ListenerPhase;
import com.github.imdmk.spenttime.user.UserFactory;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

public class PlaytimeModule implements PluginModule {

    private PlaytimeService playtimeService;
    private UserFactory userFactory;

    @Override
    public void bind(@NotNull Resources resources) {
        resources.on(PlaytimeService.class).assignInstance(() -> this.playtimeService);
        resources.on(UserFactory.class).assignInstance(() -> this.userFactory);
    }

    @Override
    public void init(@NotNull Injector injector) {
        this.playtimeService = injector.newInstance(BukkitPlaytimeService.class);
        this.userFactory = injector.newInstance(PlaytimeUserFactory.class);
    }

    @Override
    public CommandPhase commands(@NotNull Injector injector) {
        return configurer -> configurer.registerCommands(
                injector.newInstance(TimeCommand.class),
                injector.newInstance(TimeSetCommand.class),
                injector.newInstance(TimeTopCommand.class)
        );
    }

    @Override
    public ListenerPhase listeners(@NotNull Injector injector) {
        return configurer -> configurer.register(
                injector.newInstance(PlaytimeSaveListener.class)
        );
    }

    @Override
    public GuiPhase guis(@NotNull Injector injector) {
        return guiRegistry -> guiRegistry.register(injector.newInstance(PlaytimeTopGui.class));
    }

    @Override
    public int order() {
        return -1;
    }
}
