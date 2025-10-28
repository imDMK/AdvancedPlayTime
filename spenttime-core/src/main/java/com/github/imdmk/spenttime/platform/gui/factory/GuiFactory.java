package com.github.imdmk.spenttime.platform.gui.factory;

import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.platform.gui.config.ConfigurableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Factory for creating {@link BaseGui} instances from {@link ConfigurableGui}.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Delegate to {@link GuiBuilderFactory} based on configured type,</li>
 *   <li>Apply base attributes (e.g. title),</li>
 *   <li>Optionally allow post-creation customization via a {@link Consumer}.</li>
 * </ul>
 */
public final class GuiFactory {

    private GuiFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Builds a GUI instance from the provided configuration.
     *
     * @param config the GUI configuration
     * @return a new {@link BaseGui} instance
     * @throws IllegalArgumentException if the GUI type is unsupported
     */
    public static @NotNull BaseGui build(@NotNull ConfigurableGui config) {
        Validator.notNull(config, "config cannot be null");
        return GuiBuilderFactory.forType(config.type())
                .title(config.title())
                .create();
    }

    /**
     * Builds and immediately customizes a GUI using the provided consumer.
     *
     * @param config       the GUI configuration
     * @param editConsumer consumer to modify the GUI instance before returning
     * @return the configured {@link BaseGui}
     */
    public static @NotNull BaseGui build(@NotNull ConfigurableGui config, @NotNull Consumer<BaseGui> editConsumer) {
        Validator.notNull(config, "config cannot be null");
        Validator.notNull(editConsumer, "editConsumer cannot be null");

        BaseGui gui = build(config);
        editConsumer.accept(gui);
        return gui;
    }
}
