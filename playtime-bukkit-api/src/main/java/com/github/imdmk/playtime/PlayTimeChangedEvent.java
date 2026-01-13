package com.github.imdmk.playtime;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class PlayTimeChangedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final boolean ASYNC = true;

    private final UUID playerId;
    private final PlayTime newTime;
    private final PlayTime oldTime;

    public PlayTimeChangedEvent(
            @NotNull UUID playerId,
            @NotNull PlayTime newTime,
            @NotNull PlayTime oldTime
    ) {
        super(ASYNC);
        this.playerId = playerId;
        this.oldTime = oldTime;
        this.newTime = newTime;
    }

    @NotNull
    public UUID getPlayerId() {
        return playerId;
    }

    @NotNull
    public PlayTime getNewTime() {
        return newTime;
    }

    @NotNull
    public PlayTime getOldTime() {
        return oldTime;
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
