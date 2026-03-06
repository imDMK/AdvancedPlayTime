package com.github.imdmk.playtime.core.platform.event;

import org.bukkit.event.Event;

public interface EventCaller {

    <E extends Event> E callEvent(E event);

}
