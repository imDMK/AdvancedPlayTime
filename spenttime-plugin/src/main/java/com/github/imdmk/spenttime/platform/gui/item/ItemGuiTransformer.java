package com.github.imdmk.spenttime.platform.gui.item;

import com.github.imdmk.spenttime.Validator;
import com.github.imdmk.spenttime.platform.serdes.EnchantmentSerializer;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

/**
 * Stateless utility that converts {@link ItemGui} definitions into Triumph {@link GuiItem}s.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Translate material, name, lore, enchantments and flags to a {@link GuiItem},</li>
 *   <li>Attach a supplied click handler.</li>
 * </ul>
 *
 * <strong>Thread-safety:</strong> This is a pure transformation; it does not touch inventories.
 * However, constructing Bukkit objects off the main thread may vary by server implementation.
 * Prefer invoking on the main thread unless you have profiled and verified safety.
 */
public final class ItemGuiTransformer {

    private ItemGuiTransformer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates a {@link GuiItem} with a no-op click handler.
     *
     * @param item item definition (must be non-null and internally validated)
     * @return a new {@link GuiItem} instance
     * @throws IllegalArgumentException if {@code def} is {@code null}
     */
    public static @NotNull GuiItem toGuiItem(@NotNull ItemGui item) {
        return toGuiItem(item, event -> {});
    }

    /**
     * Creates a {@link GuiItem} and wires the provided click handler.
     *
     * @param item     item definition (non-null)
     * @param onClick click handler (non-null)
     * @return a new {@link GuiItem} instance
     * @throws IllegalArgumentException if any argument is {@code null}
     */
    public static @NotNull GuiItem toGuiItem(@NotNull ItemGui item, @NotNull GuiAction<InventoryClickEvent> onClick) {
        Validator.notNull(item, "item definition cannot be null");
        Validator.notNull(onClick, "onClick cannot be null");

        final ItemBuilder builder = ItemBuilder.from(item.material())
                .name(item.name())
                .lore(item.lore());

        final var enchantments = item.enchantments();
        if (enchantments != null && !enchantments.isEmpty()) {
            builder.enchant(enchantments);
        }

        final var flags = item.flags();
        if (flags != null && !flags.isEmpty()) {
            builder.flags(flags.toArray(new ItemFlag[0]));
        }

        return builder.asGuiItem(onClick);
    }
}
