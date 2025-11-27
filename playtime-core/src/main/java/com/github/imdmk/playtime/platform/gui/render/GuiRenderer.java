package com.github.imdmk.playtime.platform.gui.render;

import com.github.imdmk.playtime.platform.gui.item.ItemGui;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Renders and places {@link ItemGui} into {@link BaseGui} instances.
 * Invoke only on the Bukkit main thread.
 */
public interface GuiRenderer {

    @Contract(mutates = "param1")
    default void setItem(@NotNull BaseGui gui,
                         int slot,
                         @NotNull ItemGui item,
                         @NotNull RenderContext context,
                         @NotNull RenderOptions options,
                         @NotNull Consumer<InventoryClickEvent> onClick) {
        setItem(gui, slot, item, context, options, onClick, b -> {});
    }

    /**
     * Sets the item in a specific slot (overwrites existing content).
     * Supports per-slot customization via {@code builderEditor}.
     */
    @Contract(mutates = "param1")
    void setItem(@NotNull BaseGui gui,
                 int slot,
                 @NotNull ItemGui item,
                 @NotNull RenderContext context,
                 @NotNull RenderOptions options,
                 @NotNull Consumer<InventoryClickEvent> onClick,
                 @NotNull Consumer<ItemBuilder> builderEditor);

    @Contract(mutates = "param1")
    default void setItem(@NotNull BaseGui gui,
                         @NotNull ItemGui item,
                         @NotNull RenderContext context,
                         @NotNull RenderOptions options,
                         @NotNull Consumer<InventoryClickEvent> onClick) {
        var slot = item.slot();
        if (slot == null) {
            throw new IllegalArgumentException("Item slot is null (use add(...) for non-slotted items)");
        }

        setItem(gui, slot, item, context, options, onClick, b -> {});
    }

    @Contract(mutates = "param1")
    default void setItem(@NotNull BaseGui gui,
                         @NotNull ItemGui item,
                         @NotNull RenderContext context,
                         @NotNull RenderOptions options,
                         @NotNull Consumer<InventoryClickEvent> onClick,
                         @NotNull Consumer<ItemBuilder> builderEditor) {
        var slot = item.slot();
        if (slot == null) {
            throw new IllegalArgumentException("Item slot is null (use add(...) for non-slotted items)");
        }

        setItem(gui, slot, item, context, options, onClick, builderEditor);
    }

    @Contract(mutates = "param1")
    default void addItem(@NotNull BaseGui gui,
                         @NotNull ItemGui item,
                         @NotNull RenderContext context,
                         @NotNull RenderOptions options,
                         @NotNull Consumer<InventoryClickEvent> onClick) {
        addItem(gui, item, context, options, onClick, b -> {});
    }

    /**
     * Adds the item to the next free slot.
     * Supports per-slot customization via {@code builderEditor}.
     */
    @Contract(mutates = "param1")
    void addItem(@NotNull BaseGui gui,
                 @NotNull ItemGui item,
                 @NotNull RenderContext context,
                 @NotNull RenderOptions options,
                 @NotNull Consumer<InventoryClickEvent> onClick,
                 @NotNull Consumer<ItemBuilder> builderEditor);
}
