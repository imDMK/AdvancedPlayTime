package com.github.imdmk.spenttime.infrastructure.module.phase;

import com.github.imdmk.spenttime.platform.gui.GuiRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Functional phase interface responsible for GUI registration.
 * <p>
 * Implementations should register all inventory or interface GUIs
 * via the provided {@link GuiRegistry}.
 */
@FunctionalInterface
public interface GuiPhase {

    /**
     * Registers all GUIs provided by this module.
     *
     * @param guiRegistry the GUI registry used for GUI definitions and factories (never {@code null})
     */
    void register(@NotNull GuiRegistry guiRegistry);
}
