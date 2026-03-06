package com.github.imdmk.playtime.core.injector;

import java.util.Comparator;
import java.util.List;

final class ComponentSorter {

    void sort(List<Component<?>> components) {
        components.sort(Comparator
                .comparing((Component<?> c) -> c.priority())
                .thenComparingInt(Component::order)
        );
    }
}
