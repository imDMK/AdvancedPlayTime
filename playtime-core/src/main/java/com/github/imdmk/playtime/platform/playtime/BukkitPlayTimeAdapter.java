package com.github.imdmk.playtime.platform.playtime;

import com.github.imdmk.playtime.PlayTime;
import com.github.imdmk.playtime.injector.ComponentPriority;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

@Service(priority = ComponentPriority.LOW)
final class BukkitPlayTimeAdapter implements PlayTimeAdapter {

    private final TaskScheduler scheduler;

    @Inject
    BukkitPlayTimeAdapter(@NotNull TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @NotNull
    @Override
    public PlayTime read(@NotNull Player player) {
        return PlayTime.ofTicks(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
    }

    @Override
    public void write(@NotNull Player player, @NotNull PlayTime playTime) {
        scheduler.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }

            player.setStatistic(Statistic.PLAY_ONE_MINUTE, playTime.toTicks());
        });
    }
}
