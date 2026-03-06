package com.github.imdmk.playtime.core.platform.gui.view;

import com.github.imdmk.playtime.core.platform.gui.config.NavigationBarConfig;
import com.github.imdmk.playtime.core.platform.gui.render.GuiRenderer;
import com.github.imdmk.playtime.core.platform.gui.render.RenderOptions;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public abstract class AbstractGui {

    protected final NavigationBarConfig config;
    protected final TaskScheduler scheduler;
    protected final GuiRenderer renderer;
    protected final RenderOptions renderOptions;

    private final NavigationBar navigationBar;

    protected AbstractGui(
            NavigationBarConfig config,
            TaskScheduler taskScheduler,
            GuiRenderer renderer,
            RenderOptions renderOptions
    ) {
        this.config = config;
        this.scheduler = taskScheduler;
        this.renderer = renderer;
        this.renderOptions = renderOptions;

        this.navigationBar = new NavigationBar(
                this.config,
                this.scheduler,
                this.renderer,
                this.renderOptions
        );
    }

    protected void placeNext(BaseGui gui, Player viewer) {
        navigationBar.setNext(gui, viewer);
    }

    protected void placePrevious(BaseGui gui, Player viewer) {
        navigationBar.setPrevious(gui, viewer);
    }

    protected void placeExit(BaseGui gui, Player viewer, Consumer<InventoryClickEvent> exitAction) {
        navigationBar.setExit(gui, viewer, exitAction);
    }
}
