package com.github.imdmk.playtime.platform.gui.view;

import com.github.imdmk.playtime.platform.gui.config.NavigationBarConfig;
import com.github.imdmk.playtime.platform.gui.render.GuiRenderer;
import com.github.imdmk.playtime.platform.gui.render.RenderOptions;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class AbstractGui {

    protected final NavigationBarConfig config;
    protected final TaskScheduler scheduler;
    protected final GuiRenderer renderer;
    protected final RenderOptions renderOptions;

    private final NavigationBar navigationBar;

    protected AbstractGui(
            @NotNull NavigationBarConfig config,
            @NotNull TaskScheduler taskScheduler,
            @NotNull GuiRenderer renderer,
            @NotNull RenderOptions renderOptions
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

    protected void placeNext(@NotNull BaseGui gui, @NotNull Player viewer) {
        navigationBar.setNext(gui, viewer);
    }

    protected void placePrevious(@NotNull BaseGui gui, @NotNull Player viewer) {
        navigationBar.setPrevious(gui, viewer);
    }

    protected void placeExit(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull Consumer<InventoryClickEvent> exitAction) {
        navigationBar.setExit(gui, viewer, exitAction);
    }
}
