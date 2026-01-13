package com.github.imdmk.playtime.platform.gui.item;

import dev.triumphteam.gui.builder.item.BaseItemBuilder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class ItemGuiTransformer {

    private ItemGuiTransformer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static GuiItem toGuiItem(@NotNull ItemGui item) {
        return toGuiItem(item, (e) -> {}, (b) -> {});
    }

    public static GuiItem toGuiItem(@NotNull ItemGui item, @NotNull GuiAction<InventoryClickEvent> onClick) {
        return toGuiItem(item, onClick, (b) -> {});
    }

    public static GuiItem toGuiItem(@NotNull ItemGui item, @NotNull Consumer<InventoryClickEvent> onClick) {
        return toGuiItem(item, onClick::accept, (b) -> {});
    }

    public static GuiItem toGuiItem(
            @NotNull ItemGui item,
            @NotNull GuiAction<InventoryClickEvent> onClick,
            @NotNull Consumer<BaseItemBuilder<?>> builderEditor
    ) {
        final var material = item.material();
        final var builder = material == Material.PLAYER_HEAD ? ItemBuilder.skull() : ItemBuilder.from(material);

        builder.name(item.name());
        builder.lore(item.lore());

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
