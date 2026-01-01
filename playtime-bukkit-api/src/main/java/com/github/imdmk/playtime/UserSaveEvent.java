package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.User;
import com.github.imdmk.playtime.user.UserSaveReason;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class UserSaveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final boolean ASYNC = true;

    private final User user;
    private final UserSaveReason reason;

    public UserSaveEvent(@NotNull User user, @NotNull UserSaveReason reason) {
        super(ASYNC);
        this.user = user;
        this.reason = reason;
    }

    public @NotNull User getUser() {
        return this.user;
    }

    public @NotNull UserSaveReason getReason() {
        return this.reason;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
