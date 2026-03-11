package com.github.imdmk.playtime.core.platform.gui.view;

import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;

public interface ParameterizedGui<T> extends OpenableGui<T> {

    @Override
    default void open(
            Player viewer,
            TaskScheduler scheduler,
            T parameter
    ) {
        BaseGui gui = createGui(viewer, parameter);
        prepareItems(gui, viewer, parameter);
        scheduler.runSync(() -> gui.open(viewer));
    }

    BaseGui createGui(Player viewer, T parameter);

    void prepareItems(
            BaseGui gui,
            Player viewer,
            T parameter
    );
}

