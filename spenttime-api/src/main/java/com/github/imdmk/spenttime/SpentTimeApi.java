package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.user.UserService;
import org.jetbrains.annotations.NotNull;

/**
 * Central API contract for interacting with the SpentTime plugin’s core services.
 *
 * <p>This interface provides unified access to the main subsystems of the plugin:
 * <ul>
 *     <li>{@link UserService} – manages player data persistence, synchronization, and user-specific statistics.</li>
 *     <li>{@link PlaytimeService} – handles retrieval and manipulation of player playtime data.</li>
 * </ul>
 * </p>
 *
 * <p>External plugins can use this interface to integrate with SpentTime features
 * without depending on internal implementation details. The implementation is provided
 * automatically by the SpentTime plugin during runtime initialization.</p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * SpentTimeApi api = SpentTimeApiProvider.get();
 *
 * UserService userService = api.userService();
 * PlaytimeService playtimeService = api.playtimeService();
 *
 * UUID uuid = player.getUniqueId();
 * UserTime time = playtimeService.getTime(uuid);
 * }</pre>
 *
 * @see com.github.imdmk.spenttime.PlaytimeService
 * @see com.github.imdmk.spenttime.user.UserService
 * @see com.github.imdmk.spenttime.user.UserTime
 */
public interface SpentTimeApi {

    /**
     * Returns the {@link UserService}, which provides access to user management operations
     * such as creating, saving, and retrieving user data including playtime, ranks, and meta information.
     *
     * @return the non-null {@link UserService} instance
     */
    @NotNull UserService userService();

    /**
     * Returns the {@link PlaytimeService}, which provides high-level operations for
     * retrieving and modifying player playtime data.
     *
     * <p>This service is responsible for bridging the plugin’s internal user model
     * with the underlying platform or database storage.</p>
     *
     * @return the non-null {@link PlaytimeService} instance
     */
    @NotNull PlaytimeService playtimeService();
}
