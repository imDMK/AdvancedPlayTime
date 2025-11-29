package com.github.imdmk.playtime.user;

/**
 * Describes the context in which a {@link User} instance is persisted.
 *
 * <p>These reasons help services, repositories, logging and auditing systems
 * understand why a save operation took place.</p>
 *
 * <p>Typical usage: passed to {@code UserService#save(User, UserSaveReason)}
 * to provide semantic context for persistence logic.</p>
 *
 * @see User
 * @see UserService
 */
public enum UserSaveReason {

    /**
     * The player joined the server — user data is loaded or created.
     */
    PLAYER_JOIN,

    /**
     * The player left the server — user data should be persisted.
     */
    PLAYER_LEAVE,

    /**
     * An administrator explicitly set the user's playtime via command.
     */
    SET_COMMAND,

    /**
     * An administrator reset the user's playtime via command.
     */
    RESET_COMMAND,

    /**
     * The user's data was persisted by a scheduled task
     * (e.g., automatic save every 5 minutes).
     */
    SCHEDULED_SAVE,

    /**
     * The user's playtime was reset by a GUI action (e.g., button click).
     */
    GUI_RESET_CLICK
}
