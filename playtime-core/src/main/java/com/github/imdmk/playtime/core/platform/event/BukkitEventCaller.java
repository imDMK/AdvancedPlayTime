package com.github.imdmk.playtime.core.platform.event;

import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service(priority = ComponentPriority.LOW)
final class BukkitEventCaller implements EventCaller {

    private final Server server;
    private final TaskScheduler scheduler;

    @Inject
    BukkitEventCaller(Server server, TaskScheduler scheduler) {
        this.server = server;
        this.scheduler = scheduler;
    }

    @Override
    public <E extends Event> E callEvent(E event) {
        if (event.isAsynchronous() || server.isPrimaryThread()) {
            server.getPluginManager().callEvent(event);
            return event;
        }

        scheduler.runSync(() -> server.getPluginManager().callEvent(event));
        return event;
    }

}
