package com.github.imdmk.playtime.core.platform.serdes;

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
    public void serialize(Enchantment enchantment, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.setValue(enchantment.getKeyOrThrow().toString(), String.class);
    }

    @Override
    public Enchantment deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        String value = data.getValue(String.class);

        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) {
            throw new IllegalArgumentException("Invalid enchantment key: " + value);
        }

        Enchantment enchantment = Registry.ENCHANTMENT.get(key);
        if (enchantment == null) {
            throw new IllegalArgumentException("Unknown enchantment: " + value);
        }

        return enchantment;
    }
}