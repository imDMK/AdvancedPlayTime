package com.github.imdmk.spenttime.platform.gui.view;

import com.github.imdmk.spenttime.platform.gui.config.NavigationBarConfig;
import com.github.imdmk.spenttime.platform.gui.render.GuiRenderer;
import com.github.imdmk.spenttime.platform.gui.render.RenderContext;
import com.github.imdmk.spenttime.platform.gui.render.RenderOptions;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.shared.Validator;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Places navigation controls (Next, Previous, Exit) into Triumph GUIs.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Compute target slots via {@link GridSlots},</li>
 *   <li>Delegate permission/policy enforcement to {@link GuiRenderer},</li>
 *   <li>Provide short-lived feedback (e.g., "no next/previous") and restore original items.</li>
 * </ul>
 *
 * <strong>Threading:</strong> All methods are expected to be called on the Bukkit main thread.
 * The class is stateless w.r.t. rendering; it holds only injected collaborators.
 */
final class NavigationBar {

    private static final long RESTORE_DELAY_TICKS = 60L;

    private final NavigationBarConfig config;
    private final TaskScheduler scheduler;
    private final GuiRenderer renderer;
    private final RenderOptions renderOptions;

    /**
     * @param config        navigation bar config (items, etc.)
     * @param scheduler     scheduler for short delayed updates
     * @param renderer      GUI renderer enforcing permission policy
     * @param renderOptions render options (no-permission policy, onDenied)
     */
    NavigationBar(@NotNull NavigationBarConfig config,
                  @NotNull TaskScheduler scheduler,
                  @NotNull GuiRenderer renderer,
                  @NotNull RenderOptions renderOptions) {
        this.config = Validator.notNull(config, "config cannot be null");
        this.renderer = Validator.notNull(renderer, "renderer cannot be null");
        this.scheduler = Validator.notNull(scheduler, "scheduler cannot be null");
        this.renderOptions = Validator.notNull(renderOptions, "renderOptions cannot be null");
    }

    /**
     * Places the "Next page" button if {@code gui} is paginated.
     */
    void setNext(@NotNull BaseGui gui, @NotNull Player viewer) {
        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");

        if (!(gui instanceof PaginatedGui paginated)) {
            return;
        }

        final var renderContext = RenderContext.defaultContext(viewer);
        final var slot = GridSlots.next(gui.getRows());

        final Consumer<InventoryClickEvent> onClick = event -> {
            if (!paginated.next()) {
                renderer.setItem(gui, event.getSlot(), config.noNextItem, renderContext, renderOptions, this::noop);
                restoreLater(() -> setNext(gui, viewer));
            }
        };

        renderer.setItem(gui, slot, config.nextItem, renderContext, renderOptions, onClick);
    }

    /**
     * Places the "Previous page" button if {@code gui} is paginated.
     */
    void setPrevious(@NotNull BaseGui gui, @NotNull Player viewer) {
        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");

        if (!(gui instanceof PaginatedGui paginated)) {
            return;
        }

        final var renderContext = RenderContext.defaultContext(viewer);
        final var slot = GridSlots.previous(gui.getRows());

        final Consumer<InventoryClickEvent> onClick = event -> {
            if (!paginated.previous()) {
                renderer.setItem(gui, event.getSlot(), config.noPreviousItem, renderContext, renderOptions, this::noop);
                restoreLater(() -> setPrevious(gui, viewer));
            }
        };

        renderer.setItem(gui, slot, config.previousItem, renderContext, renderOptions, onClick);
    }

    /**
     * Places the "Exit" button which triggers the provided action.
     */
    void setExit(
            @NotNull BaseGui gui,
            @NotNull Player viewer,
            @NotNull Consumer<InventoryClickEvent> exit) {
        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");
        Validator.notNull(exit, "exit cannot be null");

        final var renderContext = RenderContext.defaultContext(viewer);
        final var slot = GridSlots.exit(gui.getRows());
        renderer.setItem(gui, slot, config.exitItem, renderContext, renderOptions, exit);
    }

    /**
     * Schedules a short delayed restore action (e.g., after showing "no next/previous").
     */
    private void restoreLater(@NotNull Runnable restoreAction) {
        Validator.notNull(restoreAction, "restoreAction cannot be null");
        scheduler.runLaterSync(restoreAction, RESTORE_DELAY_TICKS);
    }

    private void noop(@NotNull InventoryClickEvent e) {
        // intentionally no-op
    }
}
