package com.github.imdmk.playtime.core.platform.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

public final class SoundSerializer implements ObjectSerializer<Sound> {

    @Override
    public boolean supports(Class<? super Sound> type) {
        return Sound.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(Sound sound, SerializationData data, GenericsDeclaration generics) {
//        NamespacedKey key = Registry.SOUNDS.getKey(sound);
//        if (key == null) {
//            return;
//        }
//
//        data.setValue(key.getKey(), String.class);
    }

    @Override
    public Sound deserialize(DeserializationData data, GenericsDeclaration generics) {
        return Registry.SOUNDS.get(NamespacedKey.minecraft(data.getValue(String.class)));
    }
}
