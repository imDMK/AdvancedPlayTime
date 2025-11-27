package com.github.imdmk.playtime.platform.gui.view;

import com.github.imdmk.playtime.platform.gui.config.GuiConfig;
import com.github.imdmk.playtime.platform.gui.config.NavigationBarConfig;
import com.github.imdmk.playtime.platform.gui.render.GuiRenderer;
import com.github.imdmk.playtime.platform.gui.render.RenderContext;
import com.github.imdmk.playtime.platform.gui.render.RenderOptions;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.shared.Validator;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Thin base for GUI implementations.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Provide navigation helpers (Next/Previous/Exit),</li>
 *   <li>Hold shared collaborators: {@link GuiConfig}, {@link TaskScheduler},
 *       {@link GuiRenderer}, {@link RenderContext}, {@link RenderOptions}.</li>
 * </ul>
 *
 * <strong>Threading:</strong> All methods are expected to be called on the Bukkit main thread.
 */
public abstract class AbstractGui {

    protected final NavigationBarConfig config;
    protected final TaskScheduler scheduler;
    protected final GuiRenderer renderer;
    protected final RenderOptions renderOptions;

    private final NavigationBar navigationBar;

    /**
     * @param config         GUI config (visual defaults, nav items, etc.)
     * @param taskScheduler  scheduler for short, sync GUI updates
     * @param renderer    renderer that places items and enforces permission policy
     * @param renderOptions  render options (no-permission policy, onDenied)
     */
    protected AbstractGui(@NotNull NavigationBarConfig config,
                          @NotNull TaskScheduler taskScheduler,
                          @NotNull GuiRenderer renderer,
                          @NotNull RenderOptions renderOptions) {
        this.config = Validator.notNull(config, "config cannot be null");
        this.scheduler = Validator.notNull(taskScheduler, "taskScheduler cannot be null");
        this.renderer = Validator.notNull(renderer, "renderer cannot be null");
        this.renderOptions = Validator.notNull(renderOptions, "renderOptions cannot be null");

        this.navigationBar = new NavigationBar(
                this.config,
                this.scheduler,
                this.renderer,
                this.renderOptions
        );
    }

    /**
     * Places the "Next" control if the GUI is paginated.
     *
     * @param gui  target GUI
     * @param viewer target viewer
     */
    protected void placeNext(@NotNull BaseGui gui, @NotNull Player viewer) {
        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");
        navigationBar.setNext(gui, viewer);
    }

    /**
     * Places the "Previous" control if the GUI is paginated.
     *
     * @param gui  target GUI
     * @param viewer target viewer
     */
    protected void placePrevious(@NotNull BaseGui gui, @NotNull Player viewer) {
        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");
        navigationBar.setPrevious(gui, viewer);
    }

    /**
     * Places the "Exit" control.
     *
     * @param gui  target GUI
     * @param viewer target viewer
     * @param exit action to run on click
     */
    protected void placeExit(
            @NotNull BaseGui gui,
            @NotNull Player viewer,
            @NotNull Consumer<InventoryClickEvent> exit) {
        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(exit, "exit cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");
        navigationBar.setExit(gui, viewer, exit);
    }
}
