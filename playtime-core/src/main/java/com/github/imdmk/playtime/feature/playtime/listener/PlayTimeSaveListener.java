package com.github.imdmk.playtime.feature.playtime.listener;

import com.github.imdmk.playtime.PlaytimeService;
import com.github.imdmk.playtime.UserPreSaveEvent;
import com.github.imdmk.playtime.UserSaveEvent;
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserSaveReason;
import com.github.imdmk.playtime.user.UserTime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

public final class PlayTimeSaveListener implements Listener {

    private final PlaytimeService playtimeService;

    @Inject
    public PlayTimeSaveListener(@NotNull PlaytimeService playtimeService) {
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUserPreSave(UserPreSaveEvent event) {
        final User user = event.getUser();
        final UUID uuid = user.getUuid();
        final UserSaveReason reason = event.getReason();

        if (reason == UserSaveReason.PLAYER_LEAVE) {
            final UserTime currentPlaytime = playtimeService.getTime(uuid);
            user.setPlaytime(currentPlaytime);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUserSave(UserSaveEvent event) {
        final User user = event.getUser();
        playtimeService.setTime(user.getUuid(), user.getPlaytime());
    }
}
