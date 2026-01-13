package com.github.imdmk.playtime.injector.subscriber;

import com.github.imdmk.playtime.injector.subscriber.event.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public interface Publisher {

    void subscribe(@NotNull Object subscriber);

    <E extends SubscribeEvent> E publish(@NotNull E event);

}
