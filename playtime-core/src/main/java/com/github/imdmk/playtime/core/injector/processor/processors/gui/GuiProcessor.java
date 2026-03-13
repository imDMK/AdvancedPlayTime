package com.github.imdmk.playtime.core.injector.processor.processors.gui;

import com.github.imdmk.playtime.core.injector.annotations.gui.Gui;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessor;
import com.github.imdmk.playtime.core.injector.processor.ComponentProcessorContext;
import com.github.imdmk.playtime.core.platform.gui.GuiRegistry;
import com.github.imdmk.playtime.core.platform.gui.IdentifiableGui;
import org.panda_lang.utilities.inject.annotations.Inject;

public final class GuiProcessor implements ComponentProcessor<Gui> {

    private final GuiRegistry guiRegistry;

    @Inject
    public GuiProcessor(GuiRegistry guiRegistry) {
        this.guiRegistry = guiRegistry;
    }

    @Override
    public Class<Gui> annotation() {
        return Gui.class;
    }

    @Override
    public void process(
            Object instance,
            Gui annotation,
            ComponentProcessorContext context
    ) {
        IdentifiableGui identifiableGui = requireInstance(
                instance,
                IdentifiableGui.class,
                Gui.class
        );

        guiRegistry.register(identifiableGui);
    }
}
