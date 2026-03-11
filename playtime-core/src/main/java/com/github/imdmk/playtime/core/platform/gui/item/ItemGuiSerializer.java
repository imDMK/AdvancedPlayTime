package com.github.imdmk.playtime.core.platform.gui.item;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class ItemGuiSerializer implements ObjectSerializer<ItemGui> {

    @Override
    public boolean supports(@NotNull Class<? super ItemGui> type) {
        return ItemGui.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(ItemGui item, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("material", item.material(), Material.class);

        data.add("name", item.name(), Component.class);
        data.addCollection("lore", item.lore(), Component.class);

        Integer slot = item.slot();
        if (slot != null) {
            data.add("slot", slot, Integer.class);
        }

        Map<Enchantment, Integer> enchantments = item.enchantments();
        if (enchantments != null && !enchantments.isEmpty()) {
            data.addAsMap("enchantments", item.enchantments(), Enchantment.class, Integer.class);
        }

        List<ItemFlag> flags = item.flags();
        if (flags != null && !flags.isEmpty()) {
            data.addCollection("flags", flags, ItemFlag.class);
        }

        String permission = item.requiredPermission();
        if (permission != null && !permission.isBlank()) {
            data.add("permission", permission, String.class);
        }
    }

    @Override
    public ItemGui deserialize(DeserializationData data, GenericsDeclaration generics) {
        Material material = data.get("material", Material.class);
        Component name = data.get("name", Component.class);
        List<Component> lore = data.getAsList("lore", Component.class);

        Integer slot = data.get("slot", Integer.class);
        Map<Enchantment, Integer> enchantments = data.getAsMap("enchantments", Enchantment.class, Integer.class);
        List<ItemFlag> flags = data.getAsList("flags", ItemFlag.class);
        String permission = data.get("permission", String.class);

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
