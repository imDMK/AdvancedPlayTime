package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.user.UserService;
import org.jetbrains.annotations.NotNull;

/**
 * Primary entry point for accessing the SpentTime plugin API.
 *
 * <p>This interface provides access to the core services of the plugin,
 * including the {@link UserService} responsible for managing user data
 * and spent-time statistics.</p>
 *
 * <p>Implementations are provided by the SpentTime plugin at runtime.
 * External plugins should retrieve an instance through the plugin manager
 * or a dedicated API accessor.</p>
 */
public interface SpentTimeApi {

    /**
     * Returns the {@link UserService}, which provides operations for managing
     * and retrieving user time data.
     *
     * @return the non-null {@link UserService} instance
     */
    @NotNull UserService userService();

    @NotNull PlaytimeService playtimeService();
}
