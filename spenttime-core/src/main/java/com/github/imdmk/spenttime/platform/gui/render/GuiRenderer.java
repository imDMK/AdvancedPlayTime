package com.github.imdmk.spenttime.platform.gui.render;

import com.github.imdmk.spenttime.platform.gui.item.ItemGui;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Renders and places {@link ItemGui} into a {@link BaseGui}.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Resolve slot (if provided by the item),</li>
 *   <li>Delegate to the concrete implementation to perform permission-aware placement,</li>
 *   <li>Keep API ergonomic via the default method.</li>
 * </ul>
 *
 * <strong>Thread-safety:</strong> GUI mutations must be invoked on the Bukkit main thread.
 * Implementations are expected to be stateless and reusable.
 */
public interface GuiRenderer {

    /**
     * Places the given item using its declared slot.
     *
     * @throws IllegalArgumentException if {@code item.slot()} is {@code null}
     */
    @Contract(mutates = "param1")
    default void put(@NotNull BaseGui gui,
                     @NotNull ItemGui item,
                     @NotNull RenderContext context,
                     @NotNull RenderOptions options,
                     @NotNull Consumer<InventoryClickEvent> onClick) {
        if (item.slot() == null) {
            throw new IllegalArgumentException("Item slot is null");
        }

        put(gui, item.slot(), item, context, options, onClick);
    }

    /**
     * Places the given item into the GUI at the specified slot, honoring {@link RenderOptions}
     * (e.g., permission policy) and wiring the provided click handler.
     *
     * @param gui     target GUI
     * @param slot    0-based inventory slot
     * @param item    item definition
     * @param context rendering context (viewer, permission evaluator)
     * @param options rendering options (policy, onDenied handler)
     * @param onClick click handler (executed only when allowed by policy)
     */
    @Contract(mutates = "param1")
    void put(@NotNull BaseGui gui,
             int slot,
             @NotNull ItemGui item,
             @NotNull RenderContext context,
             @NotNull RenderOptions options,
             @NotNull Consumer<InventoryClickEvent> onClick);
}
