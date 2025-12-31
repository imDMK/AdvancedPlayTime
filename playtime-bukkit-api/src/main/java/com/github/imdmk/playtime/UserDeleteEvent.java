package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.UserDeleteResult;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Fired after a user deletion attempt completes.
 * <p>
 * Carries a {@link UserDeleteResult} with the deleted user snapshot (if any)
 * and the {@link com.github.imdmk.playtime.user.UserDeleteStatus} outcome.
 * </p>
 * <p><b>Threading:</b> Dispatched synchronously on the main server thread.</p>
 */
public final class UserDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final boolean ASYNC = true;

    private final UserDeleteResult result;

    /**
     * Creates a new {@code UserDeleteEvent}.
     *
     * @param result non-null deletion result
     */
    public UserDeleteEvent(@NotNull UserDeleteResult result) {
        super(ASYNC);
        this.result = Objects.requireNonNull(result, "result");
    }

    /**
     * Returns the result of the deletion operation, including the status and an
     * optional snapshot of the deleted user (if available).
     *
     * @return non-null {@link UserDeleteResult} representing the outcome of the deletion
     */
    public @NotNull UserDeleteResult getResult() {
        return this.result;
    }

    /**
     * Returns the handler list used internally by Bukkit to register and manage
     * listeners for this event.
     *
     * @return non-null static {@link HandlerList} for this event type
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Returns the static handler list for this event type.
     * <p>
     * This method is required by the Bukkit event framework and allows Bukkit
     * to correctly map event handlers to this event class.
     *
     * @return non-null static {@link HandlerList}
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

}
