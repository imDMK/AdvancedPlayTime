package com.github.imdmk.playtime.feature.playtime.listener;

import com.github.imdmk.playtime.PlayTimeService;
import com.github.imdmk.playtime.injector.annotations.Controller;
import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserSaveReason;
import com.github.imdmk.playtime.user.UserService;
import com.github.imdmk.playtime.PlayTime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

@Controller
public final class PlayTimeSaveController implements Listener {

    private final UserService userService;
    private final PlayTimeService playtimeService;

    @Inject
    public PlayTimeSaveController(
            @NotNull UserService userService,
            @NotNull PlayTimeService playtimeService
    ) {
        this.userService = userService;
        this.playtimeService = playtimeService;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        final User user = userService.findCachedByUuid(uuid).orElseThrow();
        final PlayTime time = playtimeService.getTime(uuid);

        user.setPlaytime(time);
        userService.save(user, UserSaveReason.PLAYER_LEAVE);
    }
}
