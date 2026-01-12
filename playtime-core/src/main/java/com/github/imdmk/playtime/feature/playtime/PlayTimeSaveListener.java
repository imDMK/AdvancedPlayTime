package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.injector.annotations.Controller;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

@Controller
final class PlayTimeSaveListener implements Listener {

    private static final Statistic PLAYTIME_STATISTIC = Statistic.PLAY_ONE_MINUTE;

    private final PlayTimeUserService userService;

    @Inject
    PlayTimeSaveListener(PlayTimeUserService userService) {
        this.userService = userService;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        final PlayTimeUser user = userService.getOrCreate(
                uuid,
                PlayTime.ofTicks(player.getStatistic(PLAYTIME_STATISTIC))
        );

        player.setStatistic(
                PLAYTIME_STATISTIC,
                user.getPlayTime().toTicks()
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        final int playTimeTicks = player.getStatistic(PLAYTIME_STATISTIC);
        final PlayTime playTime = PlayTime.ofTicks(playTimeTicks);

        userService.setPlayTime(uuid, playTime);
    }

}
