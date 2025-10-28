package com.github.imdmk.spenttime.platform.listener.user;

import com.github.imdmk.spenttime.PlaytimeService;
import com.github.imdmk.spenttime.UserSaveEvent;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserTime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

public class UserSaveListener implements Listener {

    @Inject private PlaytimeService playtimeService;
    @Inject private TaskScheduler taskScheduler;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUserSave(UserSaveEvent event) {
        final User user = event.getUser();

        final UUID uuid = user.getUuid();
        final UserTime time = user.getSpentTime();

        this.taskScheduler.runSync(() -> this.playtimeService.setTime(uuid, time));
    }
}
