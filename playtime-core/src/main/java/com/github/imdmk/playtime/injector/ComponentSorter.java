package com.github.imdmk.playtime.injector;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

final class ComponentSorter {

    void sort(@NotNull List<Component<?>> components) {
        components.sort(Comparator
                .comparing((Component<?> c) -> c.priority())
                .thenComparingInt(Component::order)
                .thenComparing(c -> c.getClass().getName())
        );
    }
}
