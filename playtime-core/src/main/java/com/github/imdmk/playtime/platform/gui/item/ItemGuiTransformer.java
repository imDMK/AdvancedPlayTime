package com.github.imdmk.playtime.platform.gui.item;

import com.github.imdmk.playtime.shared.Validator;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Stateless utility that converts {@link ItemGui} definitions into Triumph {@link GuiItem}s.
 *
 * <p><strong>Thread-safety:</strong> Pure transformation; prefer main thread for Bukkit objects.</p>
 */
public final class ItemGuiTransformer {

    private ItemGuiTransformer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates a {@link GuiItem} with a no-op click handler.
     *
     * @param item item definition (non-null)
     * @return a new {@link GuiItem} instance
     * @throws IllegalArgumentException if {@code item} is {@code null}
     */
    public static @NotNull GuiItem toGuiItem(@NotNull ItemGui item) {
        return toGuiItem(item, (e) -> {}, (b) -> {});
    }

    /**
     * Creates a {@link GuiItem} wiring a {@link GuiAction} click handler.
     *
     * @param item    item (non-null)
     * @param onClick click handler (non-null)
     * @return a new {@link GuiItem} instance
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public static @NotNull GuiItem toGuiItem(@NotNull ItemGui item, @NotNull GuiAction<InventoryClickEvent> onClick) {
        return toGuiItem(item, onClick, (b) -> {});
    }

    /**
     * Creates a {@link GuiItem} wiring a standard {@link Consumer} click handler.
     * Convenience overload that adapts to Triumph's {@link GuiAction}.
     *
     * @param item    item (non-null)
     * @param onClick click handler (non-null)
     * @return a new {@link GuiItem} instance
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public static @NotNull GuiItem toGuiItem(@NotNull ItemGui item, @NotNull Consumer<InventoryClickEvent> onClick) {
        return toGuiItem(item, onClick::accept, (b) -> {});
    }

    /**
     * Creates a {@link GuiItem} with handler and optional builder editor.
     *
     * @param item          item (non-null)
     * @param onClick       click handler (non-null)
     * @param builderEditor item builder editor (non-null)
     * @return a new {@link GuiItem} instance
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public static @NotNull GuiItem toGuiItem(
            @NotNull ItemGui item,
            @NotNull GuiAction<InventoryClickEvent> onClick,
            @NotNull Consumer<ItemBuilder> builderEditor) {
        Validator.notNull(item, "item cannot be null");
        Validator.notNull(onClick, "onClick cannot be null");
        Validator.notNull(builderEditor, "builderEditor cannot be null");

        final ItemBuilder builder = ItemBuilder.from(item.material())
                .name(item.name())
                .lore(item.lore());

        final var enchantments = item.enchantments();
        if (enchantments != null && !enchantments.isEmpty()) {
            builder.enchant(enchantments);
        }

        final var flags = item.flags();
        if (flags != null && !flags.isEmpty()) {
            builder.flags(flags.toArray(ItemFlag[]::new));
        }

        builderEditor.accept(builder);
        return builder.asGuiItem(onClick);
    }
}
