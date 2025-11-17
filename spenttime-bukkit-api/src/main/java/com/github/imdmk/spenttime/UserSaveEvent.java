package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserSaveReason;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Called whenever a {@link User} instance is saved by the plugin.
 *
 * <p>This event is fired after user data has been persisted.
 * It can be cancelled to prevent subsequent operations that depend on the save,
 * if applicable within the plugin's logic.</p>
 *
 * <p><b>Thread safety:</b> This event is always fired synchronously on the main server thread.</p>
 */
public final class UserSaveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final boolean ASYNC = false;

    private final User user;
    private final UserSaveReason reason;

    /**
     * Constructs a new {@code UserSaveEvent}.
     *
     * @param user the user that was saved (non-null)
     */
    public UserSaveEvent(@NotNull User user, @NotNull UserSaveReason reason) {
        super(ASYNC);
        this.user = Objects.requireNonNull(user, "user cannot be null");
        this.reason = Objects.requireNonNull(reason, "reason cannot be null");
    }

    /** @return the {@link User} involved in this event */
    public @NotNull User getUser() {
        return this.user;
    }

    /** @return the {@link UserSaveReason} involved in this event */
    public UserSaveReason getReason() {
        return this.reason;
    }

    /** @return the static {@link HandlerList} for this event type */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /** @return the static {@link HandlerList} for this event type */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
