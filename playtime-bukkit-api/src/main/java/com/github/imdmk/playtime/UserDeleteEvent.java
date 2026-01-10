package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class UserDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final boolean ASYNC = true;

    private final User user;

    public UserDeleteEvent(@NotNull User user) {
        super(ASYNC);
        this.user = user;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
