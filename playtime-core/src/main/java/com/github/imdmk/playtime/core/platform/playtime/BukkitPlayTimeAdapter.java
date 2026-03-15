package com.github.imdmk.playtime.core.platform.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.Optional;
import java.util.UUID;

@Service(priority = ComponentPriority.LOW)
final class BukkitPlayTimeAdapter implements PlayTimeAdapter {

    private final Server server;
    private final TaskScheduler scheduler;

    @Inject
    BukkitPlayTimeAdapter(Server server, TaskScheduler scheduler) {
        this.server = server;
        this.scheduler = scheduler;
    }

    @Override
    public PlayTime read(Player player) {
        return PlayTime.ofTicks(
                player.getStatistic(Statistic.PLAY_ONE_MINUTE)
        );
    }

    @Override
    @Nullable
    public PlayTime read(UUID playerId) {
        Player player = server.getPlayer(playerId);
        return player == null ? null : read(player);
    }

    @Override
    public void write(Player player, PlayTime playTime) {
        scheduler.runSyncIfNeeded(
                () -> player.setStatistic(Statistic.PLAY_ONE_MINUTE, playTime.toTicks())
        );
    }

    @Override
    public void write(UUID playerId, PlayTime playTime) {
        Player player = server.getPlayer(playerId);
        if (player != null) {
            write(player, playTime);
        }
    }
}
