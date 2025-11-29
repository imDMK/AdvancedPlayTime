package com.github.imdmk.playtime.platform.events;

import com.github.imdmk.playtime.shared.Validator;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

/**
 * Utility component responsible for registering Bukkit {@link Listener}s.
 * <p>
 * This registrar provides two registration modes:
 * <ul>
 *     <li>Direct registration of pre-instantiated listener objects.</li>
 *     <li>Automatic instantiation and field injection through {@link Injector}.</li>
 * </ul>
 * <p>
 * All listeners are registered using the plugin's {@link org.bukkit.plugin.PluginManager}.
 */
public final class BukkitListenerRegistrar {

    private final Plugin plugin;

    /**
     * Creates a new registrar for the given Bukkit plugin.
     *
     * @param plugin the plugin instance used for listener registration
     * @throws NullPointerException if the plugin is null
     */
    public BukkitListenerRegistrar(@NotNull Plugin plugin) {
        this.plugin = Validator.notNull(plugin, "plugin cannot be null");
    }

    /**
     * Registers the provided listener instances with the Bukkit {@link org.bukkit.plugin.PluginManager}.
     *
     * @param listeners the listener instances to register
     * @throws NullPointerException if the listeners array or any listener is null
     */
    public void register(@NotNull Listener... listeners) {
        Validator.notNull(listeners, "listeners cannot be null");
        for (final Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    /**
     * Creates and registers listeners using the provided {@link Injector}.
     * <p>
     * Each listener class is instantiated and its dependencies are injected automatically.
     *
     * @param injector  the dependency injector to use for listener instantiation
     * @param listeners the listener classes to create and register
     * @throws NullPointerException if the injector, the listener array, or any class is null
     */
    @SafeVarargs
    public final void register(@NotNull Injector injector, @NotNull Class<? extends Listener>... listeners) {
        Validator.notNull(injector, "injector cannot be null");
        Validator.notNull(listeners, "listeners cannot be null");

        for (final Class<? extends Listener> listenerClass : listeners) {
            register(injector.newInstance(listenerClass));
        }
    }
}
