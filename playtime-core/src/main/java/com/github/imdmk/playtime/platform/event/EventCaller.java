package com.github.imdmk.playtime.platform.event;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public interface EventCaller {

    <E extends Event> E callEvent(@NotNull E event);

}
