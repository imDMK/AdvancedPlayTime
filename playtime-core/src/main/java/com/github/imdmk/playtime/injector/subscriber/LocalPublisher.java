package com.github.imdmk.playtime.injector.subscriber;

import com.github.imdmk.playtime.injector.subscriber.event.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LocalPublisher implements Publisher {

    private final Injector injector;

    private final Map<Class<? extends SubscribeEvent>, List<SubscriberMethod>> subscribers = new HashMap<>();

    public LocalPublisher(@NotNull Injector injector) {
        this.injector = injector;
    }

    @Override
    public void subscribe(@NotNull Object instance) {
        for (final Method method : instance.getClass().getDeclaredMethods()) {
            final Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe == null) {
                continue;
            }

            final Class<? extends SubscribeEvent> eventType = subscribe.event();
            method.setAccessible(true);

            System.out.println("published class: " + instance.getClass().getName() + "method: " + method.getName() + "event: " + eventType.getName());

            subscribers
                    .computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add(new SubscriberMethod(instance, method));
        }
    }

    @Override
    public <E extends SubscribeEvent> E publish(@NotNull E event) {
        final List<SubscriberMethod> list = subscribers.get(event.getClass());
        if (list == null) {
            return event;
        }

        for (final SubscriberMethod subscriber : list) {
            final Object instance = subscriber.instance();
            final Method method = subscriber.method();
            injector.invokeMethod(method, instance, event);
        }

        return event;
    }

    private record SubscriberMethod(@NotNull Object instance, @NotNull Method method) {
    }
}

