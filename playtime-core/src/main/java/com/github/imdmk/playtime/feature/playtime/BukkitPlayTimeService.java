package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlayTimeService;
import com.github.imdmk.playtime.injector.annotations.Service;
import com.github.imdmk.playtime.injector.priority.Priority;
import com.github.imdmk.playtime.user.UserTime;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

@Service(priority = Priority.LOWEST)
final class BukkitPlayTimeService implements PlayTimeService {

    private static final Statistic PLAYTIME_STATISTIC = Statistic.PLAY_ONE_MINUTE;

    private final Server server;

    @Inject
    BukkitPlayTimeService(@NotNull Server server) {
        this.server = server;
    }

    @Override
    public @NotNull UserTime getTime(@NotNull UUID uuid) {
        checkPrimaryThread();

        int timeTicks = getOffline(uuid).getStatistic(PLAYTIME_STATISTIC);
        if (timeTicks <= 0) {
            return UserTime.ZERO;
        }

        return UserTime.ofTicks(timeTicks);
    }

    @Override
    public void setTime(@NotNull UUID uuid, @NotNull UserTime time) {
        checkPrimaryThread();
        getOffline(uuid).setStatistic(PLAYTIME_STATISTIC, time.toTicks());
    }

    @Override
    public void resetTime(@NotNull UUID uuid) {
        setTime(uuid, UserTime.ZERO);
    }

    private OfflinePlayer getOffline(UUID uuid) {
        return server.getOfflinePlayer(uuid);
    }

    private void checkPrimaryThread() {
        if (!server.isPrimaryThread()) {
            throw new UnsupportedOperationException("BukkitPlaytimeService must be called from the primary thread.");
        }
    }
}
