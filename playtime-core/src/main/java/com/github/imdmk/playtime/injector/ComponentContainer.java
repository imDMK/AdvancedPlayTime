package com.github.imdmk.playtime.injector;

import com.github.imdmk.playtime.injector.priority.Priority;
import com.github.imdmk.playtime.injector.priority.PriorityProvider;
import com.github.imdmk.playtime.shared.validate.Validator;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ComponentContainer {

    private final Object lock = new Object();

    private final EnumMap<Priority, Map<Class<? extends Annotation>, Deque<Component<?>>>> componentsByPriority = new EnumMap<>(Priority.class);

    private PriorityProvider priorityProvider;

    ComponentContainer(@NotNull PriorityProvider priorityProvider) {
        setPriorityProvider(priorityProvider);

        for (final Priority priority : Priority.values()) {
            this.componentsByPriority.put(priority, new HashMap<>());
        }
    }

    void setPriorityProvider(PriorityProvider priorityProvider) {
        this.priorityProvider = Validator.notNull(priorityProvider, "priorityProvider");
    }

    void add(Component<?> component) {
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

    List<Component<?>> consume(Class<? extends Annotation> annotation) {
        final List<Component<?>> result = new ArrayList<>();

        synchronized (this.lock) {
            for (final Priority priority : Priority.values()) { // Iteration order is defined by Priority enum declaration order
                final Deque<Component<?>> queue = this.componentsByPriority.get(priority).get(annotation);
                if (queue == null) {
                    continue;
                }

                while (!queue.isEmpty()) {
                    result.add(queue.pollFirst());
                }
            }
        }

        return result;
    }
}


