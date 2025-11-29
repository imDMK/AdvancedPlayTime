package com.github.imdmk.playtime.user;

/**
 * Enumerates all possible outcomes of a user deletion request.
 *
 * <p>This enum is typically returned as part of a {@link UserDeleteResult}
 * to describe whether the deletion succeeded, the user was missing, or
 * an internal failure occurred during the operation.</p>
 *
 * <p><strong>Usage:</strong> Used primarily by {@code UserService} or repository
 * implementations to standardize deletion responses.</p>
 *
 * @see UserDeleteResult
 * @see User
 */
public enum UserDeleteStatus {

    /**
     * The user existed and was successfully removed from persistent storage.
     */
    DELETED,

    /**
     * The user was not present in the data source at the time of deletion.
     */
    NOT_FOUND,

    /**
     * The deletion operation failed due to an unexpected exception,
     * connectivity issue, or database constraint violation.
     */
    FAILED
}
