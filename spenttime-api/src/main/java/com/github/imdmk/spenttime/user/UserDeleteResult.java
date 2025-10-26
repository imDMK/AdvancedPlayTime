package com.github.imdmk.spenttime.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the result of a user deletion operation.
 * <p>
 * Contains both the deleted {@link User} instance (if found) and
 * the {@link UserDeleteStatus} describing the outcome of the operation.
 * </p>
 *
 * @param user   the deleted user, or {@code null} if the user was not found or could not be deleted
 * @param status the result status of the deletion operation
 */
public record UserDeleteResult(@Nullable User user, @NotNull UserDeleteStatus status) {

    /**
     * Returns {@code true} if the deletion was successful and the user existed.
     *
     * @return {@code true} if the user was successfully deleted; {@code false} otherwise
     */
    public boolean isSuccess() {
        return this.user != null && this.status == UserDeleteStatus.DELETED;
    }
}
