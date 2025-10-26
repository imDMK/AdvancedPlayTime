package com.github.imdmk.spenttime.platform.events;

import com.github.imdmk.spenttime.Validator;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Utility wrapper for safely firing Bukkit {@link Event}s.
 * <p>
 * Provides a single entry point for plugin-wide event dispatching,
 * ensuring null safety and consistent access to the {@link Server}'s
 * {@link org.bukkit.plugin.PluginManager}.
 */
public final class BukkitEventCaller {

    private final Server server;

    /**
     * Creates a new event caller bound to the given server instance.
     *
     * @param server the Bukkit {@link Server} used to dispatch events
     */
    public BukkitEventCaller(@NotNull Server server) {
        this.server = Validator.notNull(server, "server cannot be null");
    }

    /**
     * Calls the specified Bukkit event synchronously on the main thread.
     *
     * @param event the event to fire
     * @param <T>   the type of the event
     * @return the same event instance after being fired
     * @throws NullPointerException if {@code event} is {@code null}
     */
    public <T extends Event> T callEvent(@NotNull T event) {
        Validator.notNull(event, "event cannot be null");
        this.server.getPluginManager().callEvent(event);
        return event;
    }

}
