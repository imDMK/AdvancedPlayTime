package com.github.imdmk.playtime.user;

/**
 * Enumerates the possible reasons for persisting a {@link User} instance.
 *
 * <p>This enum is typically used by services or repositories handling user data
 * to describe the context under which a save or update operation occurs.
 * It provides semantic clarity for logging, auditing, or event-based triggers.</p>
 *
 * <p><strong>Usage:</strong> Passed to user-saving routines (e.g. {@code UserService#save(User, UserSaveReason)})
 * to indicate why the user's state was persisted.</p>
 *
 * @see User
 * @see UserService
 */
public enum UserSaveReason {

    /**
     * The player joined the server — user data should be loaded or created.
     */
    PLAYER_JOIN,

    /**
     * The player left the server — user data should be persisted to storage.
     */
    PLAYER_LEAVE,

    /**
     * The user's time was explicitly set via a command.
     */
    SET_COMMAND,

    /**
     * The user's time was reset via a command.
     */
    RESET_COMMAND,

    /**
     * The user's time was reset through a GUI interaction (e.g., button click).
     */
    GUI_RESET_CLICK,
}
