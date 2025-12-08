package com.github.imdmk.playtime.platform.events;

import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.shared.validate.Validator;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Utility wrapper for safely firing Bukkit {@link Event}s.
 * Ensures that synchronous events are always fired on the primary server thread.
 */
public final class BukkitEventCaller {

    private final Server server;
    private final TaskScheduler scheduler;

    public BukkitEventCaller(@NotNull Server server, @NotNull TaskScheduler scheduler) {
        this.server = Validator.notNull(server, "server cannot be null");
        this.scheduler = Validator.notNull(scheduler, "scheduler cannot be null");
    }

    /**
     * Calls the specified Bukkit event ensuring correct thread usage:
     * <ul>
     *     <li>Asynchronous events are fired on the current thread;</li>
     *     <li>Synchronous events are fired on the primary server thread.</li>
     * </ul>
     */
    public <T extends Event> T callEvent(@NotNull T event) {
        Validator.notNull(event, "event cannot be null");

        if (event.isAsynchronous()) {
            server.getPluginManager().callEvent(event);
            return event;
        }

        if (server.isPrimaryThread()) {
            server.getPluginManager().callEvent(event);
        } else {
            scheduler.runSync(() -> server.getPluginManager().callEvent(event));
        }

        return event;
    }
}
