package com.github.imdmk.playtime.core.platform.gui.factory;

import com.github.imdmk.playtime.core.platform.gui.config.ConfigurableGui;
import dev.triumphteam.gui.guis.BaseGui;

import java.util.function.Consumer;

public final class GuiFactory {

    private GuiFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static BaseGui build(ConfigurableGui config) {
        return GuiBuilderFactory.forType(config.type(), config.rows())
                .title(config.title())
                .create();
    }

    public static BaseGui build(ConfigurableGui config, Consumer<BaseGui> editConsumer) {
        final BaseGui gui = build(config);
        editConsumer.accept(gui);
        return gui;
    }
}
