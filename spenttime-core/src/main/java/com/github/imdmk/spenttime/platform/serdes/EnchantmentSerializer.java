package com.github.imdmk.spenttime.platform.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public final class EnchantmentSerializer implements ObjectSerializer<Enchantment> {

    @Override
    public boolean supports(@NotNull Class<? super Enchantment> type) {
        return Enchantment.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull Enchantment enchantment, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.setValue(enchantment.getKey().getKey(), String.class);
    }

    @Override
    public Enchantment deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        return Registry.ENCHANTMENT.get(
                NamespacedKey.minecraft(data.getValue(String.class))
        );
    }
}
