package com.github.imdmk.playtime.injector;

import com.github.imdmk.playtime.injector.priority.Priority;
import com.github.imdmk.playtime.injector.priority.PriorityProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ComponentQueue {

    private final Object lock = new Object();

    private final EnumMap<Priority, Map<Class<? extends Annotation>, Deque<Component<?>>>> componentsByPriority = new EnumMap<>(Priority.class);

    private PriorityProvider priorityProvider;

    ComponentQueue(@NotNull PriorityProvider priorityProvider) {
        setPriorityProvider(priorityProvider);

        for (final Priority priority : Priority.values()) {
            this.componentsByPriority.put(priority, new HashMap<>());
        }
    }

    void setPriorityProvider(@NotNull PriorityProvider priorityProvider) {
        this.priorityProvider = priorityProvider;
    }

    void add(@NotNull Component<?> component) {
        final Priority priority = this.priorityProvider.apply(component);

        synchronized (this.lock) {
            this.componentsByPriority
                    .get(priority)
                    .computeIfAbsent(
                            component.annotation().annotationType(),
                            a -> new ArrayDeque<>()
                    )
                    .addLast(component);
        }
    }

    List<Component<?>> drain(
            @NotNull Priority priority,
            @NotNull Class<? extends Annotation> annotation
    ) {
        final List<Component<?>> result = new ArrayList<>();

        synchronized (this.lock) {
            final Deque<Component<?>> queue = this.componentsByPriority.get(priority).get(annotation);
            if (queue == null) {
                return result;
            }

            while (!queue.isEmpty()) {
                result.add(queue.pollFirst());
            }
        }

        return result;
    }
}


