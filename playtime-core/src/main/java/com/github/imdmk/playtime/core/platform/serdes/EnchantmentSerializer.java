package com.github.imdmk.playtime.core.platform.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public final class EnchantmentSerializer implements ObjectSerializer<Enchantment> {

    @Override
    public boolean supports(Class<? super Enchantment> type) {
        return Enchantment.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(Enchantment enchantment, SerializationData data, GenericsDeclaration generics) {
        data.setValue(enchantment.getKey().getKey(), String.class);
    }

    @Override
    public Enchantment deserialize(DeserializationData data, GenericsDeclaration generics) {
        //return RegistryAccess.registryAccess()
         //       .getRegistry(RegistryKey.ENCHANTMENT)
        //        .get(NamespacedKey.minecraft(data.getValue(String.class)));
        return null;
    }
}
