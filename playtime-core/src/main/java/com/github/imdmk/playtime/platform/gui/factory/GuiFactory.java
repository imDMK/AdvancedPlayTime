package com.github.imdmk.playtime.platform.gui.factory;

import com.github.imdmk.playtime.platform.gui.config.ConfigurableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class GuiFactory {

    private GuiFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static BaseGui build(@NotNull ConfigurableGui config) {
        return GuiBuilderFactory.forType(config.type(), config.rows())
                .title(config.title())
                .create();
    }

    public static BaseGui build(@NotNull ConfigurableGui config, @NotNull Consumer<BaseGui> editConsumer) {
        final BaseGui gui = build(config);
        editConsumer.accept(gui);
        return gui;
    }
}
