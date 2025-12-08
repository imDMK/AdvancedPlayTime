package com.github.imdmk.playtime.feature.playtime;

import com.github.imdmk.playtime.PlaytimeService;
import com.github.imdmk.playtime.shared.validate.Validator;
import com.github.imdmk.playtime.user.UserTime;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.UUID;

/**
 * Implementation of {@link PlaytimeService} that retrieves and modifies player playtime data
 * directly from the Bukkit {@link Server} statistics API.
 * <p>
 * This service operates exclusively on the primary (main) thread of the Bukkit server.
 * Any access attempt from a non-primary thread will result in an {@link UnsupportedOperationException}.
 * <p>
 * The playtime is based on {@link Statistic#PLAY_ONE_MINUTE}, which internally stores values
 * in Minecraft ticks (20 ticks = 1 second). The {@link UserTime} abstraction is used to
 * convert between ticks and higher-level time units.
 * <p>
 * <strong>Thread-safety note:</strong> Bukkit statistic access is not thread-safe.
 * Always ensure that invocations are done synchronously on the main thread.
 */
final class BukkitPlayTimeService implements PlaytimeService {

    private static final Statistic PLAYTIME_STATISTIC = Statistic.PLAY_ONE_MINUTE;
    private static final UserTime ZERO_TIME = UserTime.ZERO;

    private final Server server;

    @Inject
    BukkitPlayTimeService(@NotNull Server server) {
        this.server = Validator.notNull(server, "server");
    }

    /**
     * Retrieves the total playtime of the specified player.
     *
     * @param uuid the UUID of the target player (must not be {@code null})
     * @return a {@link UserTime} instance representing the player’s total playtime,
     *         or {@link UserTime#ZERO} if the player has never joined or has zero ticks recorded
     * @throws UnsupportedOperationException if called from a non-primary thread
     * @throws NullPointerException if {@code uuid} is {@code null}
     */
    @Override
    public @NotNull UserTime getTime(@NotNull UUID uuid) {
        Validator.notNull(uuid, "uuid cannot be null");

        if (!isPrimaryThread()) {
            throw new UnsupportedOperationException(
                    "BukkitPlaytimeService#getTime must be called from the primary thread."
            );
        }

        int timeTicks = getOffline(uuid).getStatistic(PLAYTIME_STATISTIC);
        if (timeTicks <= 0) {
            return ZERO_TIME;
        }

        return UserTime.ofTicks(timeTicks);
    }

    /**
     * Sets the total playtime of the specified player to the given value.
     *
     * @param uuid the UUID of the target player (must not be {@code null})
     * @param time the desired new total playtime (must not be {@code null})
     * @throws UnsupportedOperationException if called from a non-primary thread
     * @throws NullPointerException if any argument is {@code null}
     */
    @Override
    public void setTime(@NotNull UUID uuid, @NotNull UserTime time) {
        Validator.notNull(uuid, "uuid cannot be null");
        Validator.notNull(time, "time cannot be null");

        if (!isPrimaryThread()) {
            throw new UnsupportedOperationException(
                    "BukkitPlaytimeService#setTime must be called from the primary thread."
            );
        }

        getOffline(uuid).setStatistic(PLAYTIME_STATISTIC, time.toTicks());
    }

    /**
     * Resets the total playtime of the specified player to zero.
     *
     * @param uuid the UUID of the target player
     * @throws UnsupportedOperationException if called from a non-primary thread
     */
    @Override
    public void resetTime(@NotNull UUID uuid) {
        setTime(uuid, ZERO_TIME);
    }

    /**
     * Retrieves the {@link OfflinePlayer} instance associated with the given UUID.
     *
     * @param uuid the player's UUID (must not be {@code null})
     * @return the corresponding {@link OfflinePlayer} handle
     */
    private @NotNull OfflinePlayer getOffline(@NotNull UUID uuid) {
        return server.getOfflinePlayer(uuid);
    }

    /**
     * Checks whether the current execution is happening on the primary (main) Bukkit thread.
     *
     * @return {@code true} if running on the main thread, otherwise {@code false}
     */
    private boolean isPrimaryThread() {
        return server.isPrimaryThread();
    }
}
