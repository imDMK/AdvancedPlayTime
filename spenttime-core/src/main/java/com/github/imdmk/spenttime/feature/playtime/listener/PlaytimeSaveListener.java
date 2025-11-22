package com.github.imdmk.spenttime.feature.playtime.listener;

import com.github.imdmk.spenttime.PlaytimeService;
import com.github.imdmk.spenttime.UserPreSaveEvent;
import com.github.imdmk.spenttime.UserSaveEvent;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserSaveReason;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.UserTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

public class PlaytimeSaveListener implements Listener {

    private final PlaytimeService playtimeService;

    @Inject
    public PlaytimeSaveListener(@NotNull PlaytimeService playtimeService) {
        this.playtimeService = Validator.notNull(playtimeService, "playtimeService cannot be null");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUserPreSave(UserPreSaveEvent event) {
        final User user = event.getUser();
        final UUID uuid = user.getUuid();
        final UserSaveReason reason = event.getReason();

        if (reason == UserSaveReason.LEAVE) {
            final UserTime currentPlaytime = playtimeService.getTime(uuid);
            user.setSpentTime(currentPlaytime);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUserSave(UserSaveEvent event) {
        final User user = event.getUser();
        playtimeService.setTime(user.getUuid(), user.getSpentTime());
    }
}
