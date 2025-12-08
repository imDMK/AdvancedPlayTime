package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlaytimeService;
import com.github.imdmk.playtime.feature.playtime.command.TimeCommand;
import com.github.imdmk.playtime.feature.playtime.command.TimeResetAllCommand;
import com.github.imdmk.playtime.feature.playtime.command.TimeResetCommand;
import com.github.imdmk.playtime.feature.playtime.command.TimeSetCommand;
import com.github.imdmk.playtime.feature.playtime.command.TimeTopCommand;
import com.github.imdmk.playtime.feature.playtime.command.TimeTopInvalidateCommand;
import com.github.imdmk.playtime.feature.playtime.gui.PlayTimeTopGui;
import com.github.imdmk.playtime.feature.playtime.listener.PlayTimeSaveListener;
import com.github.imdmk.playtime.feature.playtime.placeholder.PlayTimePlaceholder;
import com.github.imdmk.playtime.infrastructure.module.Module;
import com.github.imdmk.playtime.infrastructure.module.phase.CommandPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.GuiPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.ListenerPhase;
import com.github.imdmk.playtime.infrastructure.module.phase.PlaceholderPhase;
import com.github.imdmk.playtime.user.UserFactory;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

public final class PlayTimeModule implements Module {

    private PlaytimeService playtimeService;
    private UserFactory userFactory;

    @Override
    public void bind(@NotNull Resources resources) {
        resources.on(PlaytimeService.class).assignInstance(() -> this.playtimeService);
        resources.on(UserFactory.class).assignInstance(() -> this.userFactory);
    }

    @Override
    public void init(@NotNull Injector injector) {
        this.playtimeService = injector.newInstance(BukkitPlayTimeService.class);
        this.userFactory = injector.newInstance(PlayTimeUserFactory.class);
    }

    @Override
    public CommandPhase commands(@NotNull Injector injector) {
        return builder -> builder.commands(
                injector.newInstance(TimeCommand.class),
                injector.newInstance(TimeSetCommand.class),
                injector.newInstance(TimeTopCommand.class),
                injector.newInstance(TimeResetCommand.class),
                injector.newInstance(TimeResetAllCommand.class),
                injector.newInstance(TimeTopInvalidateCommand.class)
        );
    }

    @Override
    public ListenerPhase listeners(@NotNull Injector injector) {
        return builder -> builder.register(
                injector.newInstance(PlayTimeSaveListener.class)
        );
    }

    @Override
    public GuiPhase guis(@NotNull Injector injector) {
        return guiRegistry -> guiRegistry.register(injector.newInstance(PlayTimeTopGui.class));
    }

    @Override
    public PlaceholderPhase placeholders(@NotNull Injector injector) {
        return adapter -> adapter.register(
                injector.newInstance(PlayTimePlaceholder.class)
        );
    }

    @Override
    public int order() {
        return -1;
    }
}
