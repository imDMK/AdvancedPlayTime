package com.github.imdmk.playtime.core.platform.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.inject.annotations.Inject;

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

    @Nullable
    @Override
    public PlayTime read(UUID playerId) {
        Player player = server.getPlayer(playerId);
        if (player == null) {
            return null;
        }

        return PlayTime.ofTicks(
                player.getStatistic(Statistic.PLAY_ONE_MINUTE)
        );
    }

    @Override
    public void write(UUID playerId, PlayTime playTime) {
        scheduler.runSync(() -> {
            Player player = server.getPlayer(playerId);
            if (player == null) {
                return;
            }

            player.setStatistic(Statistic.PLAY_ONE_MINUTE, playTime.toTicks());
        });
    }
}
