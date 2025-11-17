package com.github.imdmk.spenttime.platform.gui.view;

import com.github.imdmk.spenttime.platform.gui.GuiRegistry;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.shared.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

/**
 * Opens GUIs by id or class on the Bukkit main thread.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Lookup GUIs via {@link GuiRegistry},</li>
 *   <li>Invoke {@code BaseGui#open(Player)} on the main thread using {@link TaskScheduler}.</li>
 * </ul>
 *
 * <strong>Thread-safety:</strong> Safe to call from any thread. Actual GUI operations are marshalled to the main thread.
 */
public final class GuiOpener {

    private final GuiRegistry registry;
    private final TaskScheduler taskScheduler;

    @Inject
    public GuiOpener(@NotNull GuiRegistry registry, @NotNull TaskScheduler taskScheduler) {
        this.registry = Validator.notNull(registry, "registry cannot be null");
        this.taskScheduler = Validator.notNull(taskScheduler, "taskScheduler cannot be null");
    }

    /**
     * Opens a non-parameterized GUI by its concrete class.
     *
     * @throws IllegalArgumentException if GUI is not registered or not a {@link SimpleGui}
     */
    public void open(
            @NotNull Class<? extends SimpleGui> type,
            @NotNull Player viewer) {
        Validator.notNull(type, "type cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");

        IdentifiableGui gui = require(type);
        if (!(gui instanceof SimpleGui simpleGui)) {
            throw wrongType(type.getName(), gui, "SimpleGui");
        }

        BaseGui baseGui = simpleGui.createGui();
        simpleGui.prepareItems(baseGui, viewer);
        taskScheduler.runSync(() -> baseGui.open(viewer));
    }

    /**
     * Opens a parameterized GUI by its concrete class.
     *
     * @throws IllegalArgumentException if GUI is not registered or not a {@link ParameterizedGui}
     */
    @SuppressWarnings("unchecked")
    public <T> void open(@NotNull Class<? extends ParameterizedGui<T>> type,
                         @NotNull Player viewer,
                         @NotNull T parameter) {
        Validator.notNull(type, "type cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");
        Validator.notNull(parameter, "parameter cannot be null");

        IdentifiableGui gui = require(type);
        if (!(gui instanceof ParameterizedGui<?> paramGui)) {
            throw wrongType(type.getName(), gui, "ParameterizedGui");
        }

        ParameterizedGui<T> typed = (ParameterizedGui<T>) paramGui;
        BaseGui baseGui = typed.createGui(viewer, parameter);
        typed.prepareItems(baseGui, viewer, parameter);
        taskScheduler.runSync(() -> baseGui.open(viewer));
    }
    /**
     * Opens a non-parameterized GUI by id for the given player.
     *
     * @throws IllegalArgumentException if id is unknown or GUI is not a {@link SimpleGui}
     */
    public void open(
            @NotNull String id,
            @NotNull Player viewer) {
        Validator.notNull(id, "id cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");

        IdentifiableGui gui = require(id);
        if (!(gui instanceof SimpleGui simpleGui)) {
            throw wrongType(id, gui, "SimpleGui");
        }

        BaseGui baseGui = simpleGui.createGui();
        simpleGui.prepareItems(baseGui, viewer);
        taskScheduler.runSync(() -> baseGui.open(viewer));
    }

    /**
     * Opens a parameterized GUI by id for the given player.
     *
     * @throws IllegalArgumentException if id is unknown or GUI is not a {@link ParameterizedGui}
     */
    @SuppressWarnings("unchecked")
    public <T> void open(
            @NotNull String id,
            @NotNull Player viewer,
            @NotNull T parameter) {
        Validator.notNull(id, "id cannot be null");
        Validator.notNull(viewer, "viewer cannot be null");
        Validator.notNull(parameter, "parameter cannot be null");

        IdentifiableGui gui = require(id);
        if (!(gui instanceof ParameterizedGui<?> paramGui)) {
            throw wrongType(id, gui, "ParameterizedGui");
        }

        ParameterizedGui<T> typed = (ParameterizedGui<T>) paramGui;
        BaseGui baseGui = typed.createGui(viewer, parameter);
        typed.prepareItems(baseGui, viewer, parameter);
        taskScheduler.runSync(() -> baseGui.open(viewer));
    }

    private @NotNull IdentifiableGui require(@NotNull String id) {
        IdentifiableGui gui = registry.getById(id);
        if (gui == null) {
            throw new IllegalArgumentException("No GUI registered under id '" + id + "'");
        }
        return gui;
    }

    private @NotNull IdentifiableGui require(@NotNull Class<? extends IdentifiableGui> type) {
        IdentifiableGui gui = registry.getByClass(type);
        if (gui == null) {
            throw new IllegalArgumentException("No GUI registered for class '" + type.getName() + "'");
        }
        return gui;
    }

    private static IllegalArgumentException wrongType(String key, IdentifiableGui gui, String expected) {
        return new IllegalArgumentException(
                "GUI '" + key + "' is not a " + expected + " (actual=" + gui.getClass().getSimpleName() + ")"
        );
    }
}