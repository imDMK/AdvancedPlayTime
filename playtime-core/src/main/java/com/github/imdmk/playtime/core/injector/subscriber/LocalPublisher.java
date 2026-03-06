package com.github.imdmk.playtime.core.injector.subscriber;

import com.github.imdmk.playtime.core.injector.subscriber.event.SubscribeEvent;
import org.panda_lang.utilities.inject.Injector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LocalPublisher implements Publisher {

    private final Injector injector;

    private final Map<Class<? extends SubscribeEvent>, List<SubscriberMethod>> subscribers = new HashMap<>();

    public LocalPublisher(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void subscribe(Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe == null) {
                continue;
            }

            Class<? extends SubscribeEvent> eventType = subscribe.event();
            method.setAccessible(true);

            subscribers
                    .computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add(new SubscriberMethod(instance, method));
        }
    }

    @Override
    public <E extends SubscribeEvent> E publish(E event) {
        List<SubscriberMethod> list = subscribers.get(event.getClass());
        if (list == null) {
            return event;
        }

        for (SubscriberMethod subscriber : list) {
            Object instance = subscriber.instance();
            Method method = subscriber.method();
            injector.invokeMethod(method, instance, event);
        }

        return event;
    }

    private record SubscriberMethod(Object instance, Method method) { }
}

