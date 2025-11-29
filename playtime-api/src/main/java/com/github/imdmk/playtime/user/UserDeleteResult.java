package com.github.imdmk.playtime.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable result container representing the outcome of a user deletion attempt.
 *
 * <p>This record provides both contextual information:
 * the {@link User} instance (if it existed and was deleted) and a
 * {@link UserDeleteStatus} value describing the operation result.</p>
 *
 * <p><strong>Usage:</strong> Always check {@link #status()} to determine the deletion outcome.
 * {@link #user()} may be {@code null} if the user was not found or the operation failed.</p>
 *
 * @param user   the deleted user instance, or {@code null} if the user did not exist or was not deleted
 * @param status non-null result status representing the outcome of the deletion
 *
 * @see User
 * @see UserDeleteStatus
 */
public record UserDeleteResult(@Nullable User user, @NotNull UserDeleteStatus status) {

    /**
     * Indicates whether the deletion succeeded and the user actually existed.
     * <p>
     * This method is equivalent to checking:
     * <pre>{@code user != null && status == UserDeleteStatus.DELETED}</pre>
     *
     * @return {@code true} if the user was successfully deleted; {@code false} otherwise
     */
    public boolean isSuccess() {
        return this.user != null && this.status == UserDeleteStatus.DELETED;
    }
}
