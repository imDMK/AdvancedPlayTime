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
        data.setValue(sound.getKeyOrThrow().toString(), String.class);
    }

    @Override
    public Sound deserialize(DeserializationData data, GenericsDeclaration generics) {
        String value = data.getValue(String.class);

        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) {
            throw new IllegalArgumentException("Invalid sound key: " + value);
        }

        Sound sound = Registry.SOUNDS.get(key);
        if (sound == null) {
            throw new IllegalArgumentException("Unknown sound: " + value);
        }

        return sound;
    }
}
