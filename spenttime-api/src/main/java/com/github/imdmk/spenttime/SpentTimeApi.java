package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.user.UserService;
import org.jetbrains.annotations.NotNull;

/**
 * Central API contract for interacting with the SpentTime plugin’s core services.
 *
 * <p>This interface provides unified access to the main subsystems of the plugin:</p>
 *
 * <ul>
 *     <li>{@link UserService} – manages player data persistence, synchronization,
 *         and user-specific statistics.</li>
 *     <li>{@link PlaytimeService} – handles retrieval and manipulation of player
 *         playtime data.</li>
 * </ul>
 *
 * <p>External plugins can use this interface to integrate with SpentTime features
 * without depending on internal implementation details. The implementation is provided
 * automatically by the SpentTime plugin during runtime initialization.</p>
 *
 * <p><b>Usage Example:</b></p>
 *
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
 * @see PlaytimeService
 * @see com.github.imdmk.spenttime.user.UserService
 * @see com.github.imdmk.spenttime.user.UserTime
 */
public interface SpentTimeApi {

    /**
     * Returns the {@link UserService}, which provides access to user-management operations
     * such as creating, saving, and retrieving user data including playtime,
     * ranks, and metadata.
     *
     * @return non-null {@link UserService} instance
     */
    @NotNull UserService userService();

    /**
     * Returns the {@link PlaytimeService}, which provides high-level operations for
     * retrieving and modifying player playtime data.
     *
     * <p>This service acts as the bridge between the plugin’s internal user model
     * and the underlying storage or platform-specific systems.</p>
     *
     * @return non-null {@link PlaytimeService} instance
     */
    @NotNull PlaytimeService playtimeService();
}
