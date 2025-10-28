package com.github.imdmk.spenttime.platform.gui.view;

import com.github.imdmk.spenttime.shared.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a generic GUI that requires a parameter to be initialized and populated.
 * Acts as a template for all GUIs that are parameterized, defining a default open lifecycle.
 *
 * @param <T> the type of parameter used for populating the GUI
 */
public interface ParameterizedGui<T> extends IdentifiableGui {

    /**
     * Creates a new instance of the GUI.
     *
     * @param viewer    the player viewing the GUI
     * @param parameter the parameter used to customize the GUI
     * @return the initialized {@link BaseGui} instance
     */
    BaseGui createGui(@NotNull Player viewer, @NotNull T parameter);

    /**
     * Prepares and populates the GUI with core content based on the parameter.
     *
     * @param gui       the GUI to populate
     * @param viewer    the player viewing the GUI
     * @param parameter the context parameter
     */
    void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer, @NotNull T parameter);
}
