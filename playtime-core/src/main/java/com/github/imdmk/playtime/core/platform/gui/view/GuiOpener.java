package com.github.imdmk.playtime.core.platform.gui.view;

import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.platform.gui.GuiRegistry;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service
public final class GuiOpener {

    private final GuiRegistry registry;
    private final TaskScheduler scheduler;

    @Inject
    public GuiOpener(
            GuiRegistry registry,
            TaskScheduler scheduler
    ) {
        this.registry = registry;
        this.scheduler = scheduler;
    }

    public void open(
            Class<? extends OpenableGui<Void>> type,
            Player viewer
    ) {
        final OpenableGui<Void> gui = require(type);
        gui.open(viewer, scheduler, null);
    }

    public <T> void open(
            Class<? extends OpenableGui<T>> type,
            Player viewer,
            T parameter
    ) {
        final OpenableGui<T> gui = require(type);
        gui.open(viewer, scheduler, parameter);
    }

    private <P> OpenableGui<P> require(Class<? extends OpenableGui<P>> type) {
        final OpenableGui<P> gui = registry.getByClass(type);
        if (gui == null) {
            throw new IllegalArgumentException(
                    "No GUI registered for class '" + type.getName() + "'"
            );
        }

        return gui;
    }

}
