package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.user.UserDeleteResult;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after a user deletion attempt completes.
 * <p>
 * Carries a {@link UserDeleteResult} with the deleted user snapshot (if any)
 * and the {@link com.github.imdmk.spenttime.user.UserDeleteStatus} outcome.
 * </p>
 * <p><b>Threading:</b> Dispatched synchronously on the main server thread.</p>
 */
public final class UserDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final boolean ASYNC = false;

    private final UserDeleteResult result;

    /**
     * Creates a new {@code UserDeleteEvent}.
     *
     * @param result non-null deletion result
     */
    public UserDeleteEvent(@NotNull UserDeleteResult result) {
        super(ASYNC); // sync
        Validator.notNull(result, "result cannot be null");
        this.result = result;
    }

    /** @return the deletion result including optional user snapshot and status */
    public @NotNull UserDeleteResult getResult() {
        return this.result;
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
