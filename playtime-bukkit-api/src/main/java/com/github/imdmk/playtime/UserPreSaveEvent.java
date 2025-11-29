package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserSaveReason;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Event fired immediately before a {@link User} instance is persisted
 * to the database by the plugin.
 *
 * <p>This event serves as a <b>pre-save hook</b>, allowing listeners to
 * inspect or modify the {@link User} object before it is written to storage.
 * Any changes made to the user during this event will be included in the
 * final persisted representation.</p>
 *
 * <p><b>Note:</b> This event is <u>not cancellable</u>. It is strictly intended
 * for mutation or inspection of the user object before saving. If cancellation
 * behavior is required in the future, a dedicated cancellable event should be introduced.</p>
 *
 * <p><b>Thread safety:</b> This event is always fired synchronously on the
 * main server thread.</p>
 */
public final class UserPreSaveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final boolean ASYNC = false;

    private final User user;
    private final UserSaveReason reason;

    /**
     * Creates a new {@code UserPreSaveEvent}.
     *
     * @param user   the user about to be saved (non-null)
     * @param reason the context in which the save operation was triggered (non-null)
     */
    public UserPreSaveEvent(@NotNull User user, @NotNull UserSaveReason reason) {
        super(ASYNC);
        this.user = Objects.requireNonNull(user, "user cannot be null");
        this.reason = Objects.requireNonNull(reason, "reason cannot be null");
    }

    /**
     * Returns the {@link User} instance that will be persisted.
     * <p>Modifying this object will affect the data written to storage.</p>
     *
     * @return the user associated with this event
     */
    public @NotNull User getUser() {
        return this.user;
    }

    /**
     * Returns the reason for the save operation.
     *
     * @return a {@link UserSaveReason} describing why the user is being saved
     */
    public @NotNull UserSaveReason getReason() {
        return this.reason;
    }

    /**
     * Returns the static handler list for this event type.
     *
     * @return the handler list of this event.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Returns the static handler list for this event type.
     *
     * @return the list of handlers for this event.
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
