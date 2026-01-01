package com.github.imdmk.playtime;

import com.github.imdmk.playtime.user.UserDeleteResult;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class UserDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final boolean ASYNC = true;

    private final UserDeleteResult result;

    public UserDeleteEvent(@NotNull UserDeleteResult result) {
        super(ASYNC);
        this.result = result;
    }

    public @NotNull UserDeleteResult getResult() {
        return this.result;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

}
