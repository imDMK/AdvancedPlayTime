package com.github.imdmk.playtime.core.injector.subscriber;

import com.github.imdmk.playtime.core.injector.subscriber.event.SubscribeEvent;

public interface Publisher {

    void subscribe(Object subscriber);

    <E extends SubscribeEvent> E publish(E event);

}
