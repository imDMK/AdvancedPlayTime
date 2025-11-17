package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.user.UserTime;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A high-level abstraction for accessing and modifying player playtime data.
 * <p>
 * Implementations of this interface are responsible for bridging between
 * the plugin domain model ({@link UserTime}) and the underlying platform’s
 * data source (e.g., Bukkit statistics API, database, etc.).
 * <p>
 * Playtime is typically expressed in Minecraft ticks (20 ticks = 1 second),
 * but the {@link UserTime} abstraction handles conversions to and from human-readable units.
 *
 * @see com.github.imdmk.spenttime.user.UserTime
 */
public interface PlaytimeService {

    /**
     * Retrieves the total accumulated playtime for the specified player.
     *
     * @param uuid
     *        the unique identifier of the player whose playtime should be fetched;
     *        must not be {@code null}
     * @return
     *        a non-null {@link UserTime} representing the player’s total playtime.
     *        If no playtime is recorded or the player has never joined, returns {@link UserTime#ZERO}.
     * @throws NullPointerException
     *         if {@code uuid} is {@code null}.
     */
    @NotNull UserTime getTime(@NotNull UUID uuid);

    /**
     * Sets the total playtime for the specified player to the given value.
     *
     * @param uuid
     *        the unique identifier of the player whose playtime should be updated;
     *        must not be {@code null}
     * @param time
     *        the new total playtime value to assign; must not be {@code null}
     * @throws NullPointerException
     *         if {@code uuid} or {@code time} is {@code null}
     */
    void setTime(@NotNull UUID uuid, @NotNull UserTime time);

    /**
     * Resets the total recorded playtime of the specified player to zero.
     *
     * @param uuid
     *        the unique identifier of the player whose playtime should be reset;
     *        must not be {@code null}
     * @throws NullPointerException
     *         if {@code uuid} is {@code null}
     */
    void resetTime(@NotNull UUID uuid);
}
