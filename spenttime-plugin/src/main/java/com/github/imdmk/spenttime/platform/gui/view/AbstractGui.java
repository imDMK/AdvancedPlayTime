package com.github.imdmk.spenttime.platform.gui.view;

import com.github.imdmk.spenttime.Validator;
import com.github.imdmk.spenttime.platform.gui.config.GuiConfig;
import com.github.imdmk.spenttime.platform.gui.config.NavigationBarConfig;
import com.github.imdmk.spenttime.platform.gui.render.GuiRenderer;
import com.github.imdmk.spenttime.platform.gui.render.RenderContext;
import com.github.imdmk.spenttime.platform.gui.render.RenderOptions;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
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

    protected final GuiRenderer guiRenderer;
    protected final RenderContext renderContext;
    protected final RenderOptions renderOptions;

    private final NavigationBar navigationBar;

    /**
     * @param config         GUI config (visual defaults, nav items, etc.)
     * @param taskScheduler  scheduler for short, sync GUI updates
     * @param guiRenderer    renderer that places items and enforces permission policy
     * @param renderContext  render context (viewer + permission evaluator)
     * @param renderOptions  render options (no-permission policy, onDenied)
     */
    protected AbstractGui(@NotNull NavigationBarConfig config,
                          @NotNull TaskScheduler taskScheduler,
                          @NotNull GuiRenderer guiRenderer,
                          @NotNull RenderContext renderContext,
                          @NotNull RenderOptions renderOptions) {
        this.config = Validator.notNull(config, "config cannot be null");
        this.scheduler = Validator.notNull(taskScheduler, "taskScheduler cannot be null");
        this.guiRenderer = Validator.notNull(guiRenderer, "guiRenderer cannot be null");
        this.renderContext = Validator.notNull(renderContext, "renderContext cannot be null");
        this.renderOptions = Validator.notNull(renderOptions, "renderOptions cannot be null");

        this.navigationBar = new NavigationBar(
                this.config,
                this.guiRenderer,
                this.scheduler,
                this.renderContext,
                this.renderOptions
        );
    }

    /**
     * Places the "Next" control if the GUI is paginated.
     */
    protected void placeNext(@NotNull BaseGui gui) {
        Validator.notNull(gui, "gui cannot be null");
        this.navigationBar.setNext(gui);
    }

    /**
     * Places the "Previous" control if the GUI is paginated.
     */
    protected void placePrevious(@NotNull BaseGui gui) {
        Validator.notNull(gui, "gui cannot be null");
        this.navigationBar.setPrevious(gui);
    }

    /**
     * Places the "Exit" control.
     *
     * @param gui  target GUI
     * @param exit action to run on click
     */
    protected void placeExit(@NotNull BaseGui gui, @NotNull Consumer<InventoryClickEvent> exit) {
        Validator.notNull(gui, "gui cannot be null");
        Validator.notNull(exit, "exit cannot be null");
        this.navigationBar.setExit(gui, exit);
    }
}
