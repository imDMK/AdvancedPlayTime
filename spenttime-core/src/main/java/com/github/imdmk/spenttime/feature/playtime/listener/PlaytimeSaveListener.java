package com.github.imdmk.spenttime.feature.playtime.listener;

import com.github.imdmk.spenttime.PlaytimeService;
import com.github.imdmk.spenttime.UserSaveEvent;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

public class PlaytimeSaveListener implements Listener {

    private final PlaytimeService playtimeService;

    @Inject
    public PlaytimeSaveListener(@NotNull PlaytimeService playtimeService) {
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUserSave(UserSaveEvent event) {
        final User user = event.getUser();
        playtimeService.setTime(user.getUuid(), user.getSpentTime());
    }
}
