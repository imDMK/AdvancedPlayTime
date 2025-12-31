package com.github.imdmk.playtime.platform.gui.view;

import com.github.imdmk.playtime.platform.gui.GuiRegistry;
import com.github.imdmk.playtime.platform.gui.IdentifiableGui;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

public final class GuiOpener {

    private final GuiRegistry registry;
    private final TaskScheduler taskScheduler;

    @Inject
    public GuiOpener(@NotNull GuiRegistry registry, @NotNull TaskScheduler taskScheduler) {
        this.registry = registry;
        this.taskScheduler = taskScheduler;
    }

    public void open(
            @NotNull Class<? extends SimpleGui> type,
            @NotNull Player viewer
    ) {
        final IdentifiableGui gui = require(type);
        if (!(gui instanceof SimpleGui simpleGui)) {
            throw wrongType(type.getName(), gui, "SimpleGui");
        }

        final BaseGui baseGui = simpleGui.createGui();
        simpleGui.prepareItems(baseGui, viewer);
        taskScheduler.runSync(() -> baseGui.open(viewer));
    }

    @SuppressWarnings("unchecked")
    public <T> void open(
            @NotNull Class<? extends ParameterizedGui<T>> type,
            @NotNull Player viewer,
            @NotNull T parameter
    ) {
        final IdentifiableGui gui = require(type);
        if (!(gui instanceof ParameterizedGui<?> paramGui)) {
            throw wrongType(type.getName(), gui, "ParameterizedGui");
        }

        final ParameterizedGui<T> typed = (ParameterizedGui<T>) paramGui;
        final BaseGui baseGui = typed.createGui(viewer, parameter);

        typed.prepareItems(baseGui, viewer, parameter);
        taskScheduler.runSync(() -> baseGui.open(viewer));
    }

    public void open(
            @NotNull String id,
            @NotNull Player viewer
    ) {
        final IdentifiableGui gui = require(id);
        if (!(gui instanceof SimpleGui simpleGui)) {
            throw wrongType(id, gui, "SimpleGui");
        }

        final BaseGui baseGui = simpleGui.createGui();

        simpleGui.prepareItems(baseGui, viewer);
        taskScheduler.runSync(() -> baseGui.open(viewer));
    }

    @SuppressWarnings("unchecked")
    public <T> void open(
            @NotNull String id,
            @NotNull Player viewer,
            @NotNull T parameter
    ) {
        final IdentifiableGui gui = require(id);
        if (!(gui instanceof ParameterizedGui<?> paramGui)) {
            throw wrongType(id, gui, "ParameterizedGui");
        }

        final ParameterizedGui<T> typed = (ParameterizedGui<T>) paramGui;
        final BaseGui baseGui = typed.createGui(viewer, parameter);

        typed.prepareItems(baseGui, viewer, parameter);
        taskScheduler.runSync(() -> baseGui.open(viewer));
    }

    private IdentifiableGui require(String id) {
        final IdentifiableGui gui = registry.getById(id);
        if (gui == null) {
            throw new IllegalArgumentException("No GUI registered under id '" + id + "'");
        }
        return gui;
    }

    private IdentifiableGui require(Class<? extends IdentifiableGui> type) {
        final IdentifiableGui gui = registry.getByClass(type);
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