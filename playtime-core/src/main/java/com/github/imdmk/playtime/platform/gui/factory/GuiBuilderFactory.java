package com.github.imdmk.playtime.platform.gui.factory;

import com.github.imdmk.playtime.platform.gui.GuiType;
import com.github.imdmk.playtime.shared.validate.Validator;
import dev.triumphteam.gui.builder.gui.BaseGuiBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Factory for creating TriumphGUI {@link BaseGuiBuilder} instances
 * based on a provided {@link GuiType}.
 * <p>
 * Supports standard, paginated, and scrolling (vertical/horizontal) GUIs.
 */
public final class GuiBuilderFactory {

    private GuiBuilderFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Returns a TriumphGUI builder matching the given {@link GuiType}.
     *
     * @param type the GUI type
     * @param rows the GUI rows
     * @return a new {@link BaseGuiBuilder} instance for the given type
     * @throws IllegalArgumentException if {@code type} is {@code null}
     */
    @Contract(pure = true)
    public static @NotNull BaseGuiBuilder<?, ?> forType(@NotNull GuiType type, int rows) {
        Validator.notNull(type, "type cannot be null");

        return switch (type) {
            case STANDARD -> Gui.gui().rows(rows);
            case PAGINATED -> Gui.paginated().rows(rows);
            case SCROLLING_VERTICAL -> Gui.scrolling(ScrollType.VERTICAL).rows(rows);
            case SCROLLING_HORIZONTAL -> Gui.scrolling(ScrollType.HORIZONTAL).rows(rows);
        };
    }

    /**
     * Creates and immediately customizes a TriumphGUI builder.
     *
     * @param type          the GUI type
     * @param rows          the GUI rows
     * @param editConsumer  consumer for post-creation customization (e.g., size, disableAllInteractions)
     * @return a modified {@link BaseGuiBuilder} instance
     * @throws IllegalArgumentException if {@code type} or {@code editConsumer} is {@code null}
     */
    public static @NotNull BaseGuiBuilder<?, ?> forType(@NotNull GuiType type, int rows, @NotNull Consumer<BaseGuiBuilder<?, ?>> editConsumer) {
        Validator.notNull(type, "type cannot be null");
        Validator.notNull(editConsumer, "editConsumer cannot be null");

        BaseGuiBuilder<?, ?> builder = forType(type, rows);
        editConsumer.accept(builder);
        return builder;
    }
}
