package com.github.imdmk.playtime.platform.gui.view;

import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface SimpleGui extends OpenableGui<Void> {

    @Override
    default void open(
            @NotNull Player viewer,
            @NotNull TaskScheduler scheduler,
            Void unused
    ) {
        final BaseGui gui = createGui(viewer);
        prepareItems(gui, viewer);
        scheduler.runSync(() -> gui.open(viewer));
    }

    BaseGui createGui(@NotNull Player viewer);

    void prepareItems(
            @NotNull BaseGui gui,
            @NotNull Player viewer
    );
}
