package com.github.imdmk.playtime.core.platform.gui.view;

import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;

public interface SimpleGui extends OpenableGui<Void> {

    @Override
    default void open(
            Player viewer,
            TaskScheduler scheduler,
            Void unused
    ) {
        BaseGui gui = createGui(viewer);
        prepareItems(gui, viewer);
        scheduler.runSync(() -> gui.open(viewer));
    }

    BaseGui createGui(Player viewer);

    void prepareItems(
            BaseGui gui,
            Player viewer
    );
}
