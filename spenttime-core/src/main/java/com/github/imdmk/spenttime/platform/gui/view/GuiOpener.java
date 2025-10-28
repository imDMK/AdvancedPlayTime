package com.github.imdmk.spenttime.platform.gui.view;

import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.platform.gui.GuiRegistry;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.shared.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

/**
 * Opens GUIs by id on the Bukkit main thread.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Look up GUIs via {@link GuiRegistry},</li>
 *   <li>Invoke {@code open(...)} on the main thread using {@link TaskScheduler}.</li>
 * </ul>
 *
 * <strong>Thread-safety:</strong> Safe to call from any thread. Actual GUI operations are marshalled to main thread.
 */
public final class GuiOpener {

    @Inject private GuiRegistry registry;
    @Inject private TaskScheduler taskScheduler;

    /**
     * Opens a non-parameterized GUI by id for the given player.
     *
     * @throws IllegalArgumentException if id is unknown or GUI is not a {@link SimpleGui}
     */
    public void open(@NotNull String id, @NotNull Player viewer) {
        Validator.notNull(id, "id cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");

        IdentifiableGui gui = require(id);
        if (!(gui instanceof SimpleGui simpleGui)) {
            throw wrongType(id, gui, "SimpleGui");
        }

        BaseGui baseGui = simpleGui.createGui();
        simpleGui.prepareItems(baseGui, viewer);

        this.taskScheduler.runSync(() -> baseGui.open(viewer));
    }

    /**
     * Opens a parameterized GUI by id for the given player.
     *
     * @throws IllegalArgumentException if id is unknown or GUI is not a {@link ParameterizedGui}
     */
    @SuppressWarnings("unchecked")
    public <T> void open(@NotNull String id, @NotNull Player viewer, @NotNull T parameter) {
        Validator.notNull(id, "id cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");
        Validator.notNull(parameter, "parameter cannot be null");

        IdentifiableGui gui = require(id);
        if (!(gui instanceof ParameterizedGui<?> paramGui)) {
            throw wrongType(id, gui, "ParameterizedGui");
        }

        ParameterizedGui<T> parameterizedGui = (ParameterizedGui<T>) paramGui;

        BaseGui baseGui = parameterizedGui.createGui(viewer, parameter);
        parameterizedGui.prepareItems(baseGui, viewer, parameter);

        this.taskScheduler.runSync(() -> baseGui.open(viewer));
    }

    private IdentifiableGui require(@NotNull String id) {
        IdentifiableGui gui = this.registry.get(id);
        if (gui == null){
            throw new IllegalArgumentException("No GUI registered under id '" + id + "'");
        }

        return gui;
    }

    private static IllegalArgumentException wrongType(String id, IdentifiableGui gui, String expected) {
        return new IllegalArgumentException(
                "GUI '" + id + "' is not a " + expected + " (actual=" + gui.getClass().getSimpleName() + ")");
    }
}

