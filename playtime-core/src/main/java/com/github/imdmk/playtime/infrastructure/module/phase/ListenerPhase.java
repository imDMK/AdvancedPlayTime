package com.github.imdmk.playtime.infrastructure.module.phase;

import com.github.imdmk.playtime.platform.events.BukkitListenerRegistrar;
import org.jetbrains.annotations.NotNull;

/**
 * Functional phase interface responsible for event listener registration.
 * <p>
 * Implementations should register Bukkit {@link org.bukkit.event.Listener}s
 * using the provided {@link BukkitListenerRegistrar}.
 */
@FunctionalInterface
public interface ListenerPhase {

    /**
     * Registers all Bukkit listeners for this module.
     *
     * @param registrar the listener registrar used to bind Bukkit event listeners (never {@code null})
     */
    void register(@NotNull BukkitListenerRegistrar registrar);
}
