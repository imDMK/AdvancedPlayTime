package com.github.imdmk.playtime.core.platform.playtime;

import com.github.imdmk.playtime.api.PlayTime;
import com.github.imdmk.playtime.core.injector.ComponentPriority;
import com.github.imdmk.playtime.core.injector.annotations.Service;
import com.github.imdmk.playtime.core.platform.scheduler.TaskScheduler;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service(priority = ComponentPriority.LOW)
final class BukkitPlayTimeAdapter implements PlayTimeAdapter {

    private final TaskScheduler scheduler;

    @Inject
    BukkitPlayTimeAdapter(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public PlayTime read(Player player) {
        return PlayTime.ofTicks(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
    }

    @Override
    public void write(Player player, PlayTime playTime) {
        scheduler.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }

            player.setStatistic(Statistic.PLAY_ONE_MINUTE, playTime.toTicks());
        });
    }
}
