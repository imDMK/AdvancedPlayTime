package com.github.imdmk.playtime.platform.serdes;

import com.github.imdmk.playtime.platform.gui.item.ItemGui;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

public final class ItemGuiSerializer implements ObjectSerializer<ItemGui> {

    @Override
    public boolean supports(@NotNull Class<? super ItemGui> type) {
        return ItemGui.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull ItemGui item, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("material", item.material(), Material.class);

        data.add("name", item.name(), Component.class);
        data.addCollection("lore", item.lore(), Component.class);

        var slot = item.slot();
        if (slot != null) {
            data.add("slot", slot, Integer.class);
        }

        var enchantments = item.enchantments();
        if (enchantments != null && !enchantments.isEmpty()) {
            data.addAsMap("enchantments", item.enchantments(), Enchantment.class, Integer.class);
        }

        var flags = item.flags();
        if (flags != null && !flags.isEmpty()) {
            data.addCollection("flags", flags, ItemFlag.class);
        }

        var permission = item.requiredPermission();
        if (permission != null && !permission.isBlank()) {
            data.add("permission", permission, String.class);
        }
    }

    @Override
    public ItemGui deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        var material = data.get("material", Material.class);
        var name = data.get("name", Component.class);
        var lore = data.getAsList("lore", Component.class);

        var slot = data.get("slot", Integer.class);
        var enchantments = data.getAsMap("enchantments", Enchantment.class, Integer.class);
        var flags = data.getAsList("flags", ItemFlag.class);
        var permission = data.get("permission", String.class);

        return new ItemGui(
                material,
                name,
                lore,
                slot,
                enchantments,
                flags,
                permission
        );
    }
}
