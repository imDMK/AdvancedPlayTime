package com.github.imdmk.playtime.platform.gui.view;

import com.github.imdmk.playtime.platform.gui.IdentifiableGui;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface OpenableGui<P> extends IdentifiableGui {

    void open(
            @NotNull Player viewer,
            @NotNull TaskScheduler scheduler,
            P parameter
    );
}

