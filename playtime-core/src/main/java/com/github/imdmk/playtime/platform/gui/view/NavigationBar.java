package com.github.imdmk.playtime.platform.gui.view;

import com.github.imdmk.playtime.platform.gui.config.NavigationBarConfig;
import com.github.imdmk.playtime.platform.gui.render.GuiRenderer;
import com.github.imdmk.playtime.platform.gui.render.RenderContext;
import com.github.imdmk.playtime.platform.gui.render.RenderOptions;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Consumer;

final class NavigationBar {

    private static final Duration DELAY = Duration.ofSeconds(1);

    private final NavigationBarConfig config;
    private final TaskScheduler scheduler;
    private final GuiRenderer renderer;
    private final RenderOptions renderOptions;

    NavigationBar(
            @NotNull NavigationBarConfig config,
            @NotNull TaskScheduler scheduler,
            @NotNull GuiRenderer renderer,
            @NotNull RenderOptions renderOptions
    ) {
        this.config = config;
        this.renderer = renderer;
        this.scheduler = scheduler;
        this.renderOptions = renderOptions;
    }

    void setNext(@NotNull BaseGui gui, @NotNull Player viewer) {
        if (!(gui instanceof PaginatedGui paginated)) {
            return;
        }

        final RenderContext context = RenderContext.defaultContext(viewer);
        final int slot = GridSlots.next(gui.getRows());

        final Consumer<InventoryClickEvent> onClick = event -> {
            if (!paginated.next()) {
                renderer.setItem(gui, event.getSlot(), config.noNextItem, context, renderOptions, this::noop);
                runLater(() -> setNext(gui, viewer));
            }
        };

        renderer.setItem(gui, slot, config.nextItem, context, renderOptions, onClick);
    }

    void setPrevious(@NotNull BaseGui gui, @NotNull Player viewer) {
        if (!(gui instanceof PaginatedGui paginated)) {
            return;
        }

        final RenderContext context = RenderContext.defaultContext(viewer);
        final int slot = GridSlots.previous(gui.getRows());

        final Consumer<InventoryClickEvent> onClick = event -> {
            if (!paginated.previous()) {
                renderer.setItem(gui, event.getSlot(), config.noPreviousItem, context, renderOptions, this::noop);
                runLater(() -> setPrevious(gui, viewer));
            }
        };

        renderer.setItem(gui, slot, config.previousItem, context, renderOptions, onClick);
    }

    void setExit(
            @NotNull BaseGui gui,
            @NotNull Player viewer,
            @NotNull Consumer<InventoryClickEvent> exit
    ) {
        final RenderContext context = RenderContext.defaultContext(viewer);
        final int slot = GridSlots.exit(gui.getRows());
        renderer.setItem(gui, slot, config.exitItem, context, renderOptions, exit);
    }

    private void runLater(Runnable runnable) {
        scheduler.runLaterSync(runnable, DELAY);
    }

    private void noop(@NotNull InventoryClickEvent e) {
        // intentionally no-op
    }
}
