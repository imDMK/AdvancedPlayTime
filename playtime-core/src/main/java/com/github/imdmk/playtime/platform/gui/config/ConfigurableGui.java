package com.github.imdmk.playtime.platform.gui.config;

import com.github.imdmk.playtime.platform.gui.GuiType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a configurable GUI loaded from configuration.
 * Implementations should provide all basic GUI metadata and content definitions.
 */
public interface ConfigurableGui {

    /**
     * @return GUI title as Adventure {@link Component}
     */
    @NotNull Component title();

    /**
     * @return GUI type (e.g. {@link GuiType#STANDARD}, {@link GuiType#PAGINATED})
     */
    @NotNull GuiType type();

    /**
     * @return GUI rows
     */
    int rows();
}

