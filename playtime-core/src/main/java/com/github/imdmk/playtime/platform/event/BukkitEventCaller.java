package com.github.imdmk.playtime.platform.event;

import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.priority.Priority;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service(priority = Priority.LOW)
public final class BukkitEventCaller implements EventCaller {

    private final Server server;
    private final TaskScheduler scheduler;

    @Inject
    public BukkitEventCaller(@NotNull Server server, @NotNull TaskScheduler scheduler) {
        this.server = server;
        this.scheduler = scheduler;
    }

    @Override
    public <E extends Event> E callEvent(@NotNull E event) {
        if (event.isAsynchronous() || server.isPrimaryThread()) {
            server.getPluginManager().callEvent(event);
            return event;
        }

        scheduler.runSync(() -> server.getPluginManager().callEvent(event));
        return event;
    }

}
