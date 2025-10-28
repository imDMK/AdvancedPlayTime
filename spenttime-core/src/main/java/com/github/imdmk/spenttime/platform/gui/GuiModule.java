package com.github.imdmk.spenttime.platform.gui;

import com.github.imdmk.spenttime.infrastructure.module.PluginModule;
import com.github.imdmk.spenttime.platform.gui.render.GuiRenderer;
import com.github.imdmk.spenttime.platform.gui.render.triumph.TriumphGuiRenderer;
import com.github.imdmk.spenttime.platform.gui.view.GuiOpener;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

public class GuiModule implements PluginModule {

    private GuiRegistry guiRegistry;
    private GuiOpener guiOpener;
    private GuiRenderer guiRenderer;

    @Override
    public void bind(@NotNull Resources resources) {
        resources.on(GuiRegistry.class).assignInstance(() -> this.guiRegistry);
        resources.on(GuiOpener.class).assignInstance(() -> this.guiOpener);
        resources.on(GuiRenderer.class).assignInstance(() -> this.guiRenderer);
    }

    @Override
    public void init(@NotNull Injector injector) {
        this.guiRegistry = injector.newInstanceWithFields(GuiRegistry.class);
        this.guiOpener = injector.newInstanceWithFields(GuiOpener.class);
        this.guiRenderer = injector.newInstanceWithFields(TriumphGuiRenderer.class);
    }
}
