package com.github.imdmk.spenttime.platform.gui.view;

import com.github.imdmk.spenttime.shared.gui.IdentifiableGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a simple GUI that does not require a parameter to be created or populated.
 * Defines a standard lifecycle for opening such GUIs.
 */
public interface SimpleGui extends IdentifiableGui {

    /**
     * Creates a new instance of the GUI.
     *
     * @return the initialized {@link BaseGui} instance
     */
    BaseGui createGui();

    /**
     * Prepares and populates the GUI with its core content.
     *
     * @param gui    the GUI to populate
     * @param viewer the player viewing the GUI
     */
    void prepareItems(@NotNull BaseGui gui, @NotNull Player viewer);
}
