package com.github.imdmk.playtime.core.platform.gui.view;

import com.github.imdmk.playtime.core.platform.gui.IdentifiableGui;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import org.bukkit.entity.Player;


public interface OpenableGui<P> extends IdentifiableGui {

    void open(
            Player viewer,
            TaskScheduler scheduler,
            P parameter
    );
}

