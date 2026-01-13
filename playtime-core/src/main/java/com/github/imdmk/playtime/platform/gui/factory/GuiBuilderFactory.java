package com.github.imdmk.playtime.platform.gui.factory;

import com.github.imdmk.playtime.platform.gui.GuiType;
import dev.triumphteam.gui.builder.gui.BaseGuiBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class GuiBuilderFactory {

    private GuiBuilderFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static BaseGuiBuilder<?, ?> forType(@NotNull GuiType type, int rows) {
        return switch (type) {
            case STANDARD -> Gui.gui().rows(rows);
            case PAGINATED -> Gui.paginated().rows(rows);
            case SCROLLING_VERTICAL -> Gui.scrolling(ScrollType.VERTICAL).rows(rows);
            case SCROLLING_HORIZONTAL -> Gui.scrolling(ScrollType.HORIZONTAL).rows(rows);
        };
    }

    public static BaseGuiBuilder<?, ?> forType(@NotNull GuiType type, int rows, @NotNull Consumer<BaseGuiBuilder<?, ?>> editConsumer) {
        final BaseGuiBuilder<?, ?> builder = forType(type, rows);
        editConsumer.accept(builder);
        return builder;
    }
}
