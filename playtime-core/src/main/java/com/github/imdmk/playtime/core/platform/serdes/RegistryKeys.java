package com.github.imdmk.playtime.core.platform.serdes;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

/**
 * Reads the {@link NamespacedKey} of a registry value in a way that stays binary
 * compatible across the whole supported Spigot range.
 *
 * <p>{@code getKeyOrThrow()} only exists on {@code org.bukkit.registry.RegistryAware},
 * introduced long after 1.21, and types such as {@link org.bukkit.Sound} were plain
 * enums back then instead of interfaces. Going through {@link Keyed} — an interface in
 * every supported version — keeps the emitted call site valid on 1.21 and on 26.2.
 */
final class RegistryKeys {

    private RegistryKeys() {
    }

    static NamespacedKey keyOf(Keyed keyed) {
        NamespacedKey key = keyed.getKey();

        if (key == null) {
            throw new IllegalArgumentException("Registry value is not registered: " + keyed);
        }

        return key;
    }
}
