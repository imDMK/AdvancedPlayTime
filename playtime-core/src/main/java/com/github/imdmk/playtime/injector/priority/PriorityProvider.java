package com.github.imdmk.playtime.injector.priority;

import com.github.imdmk.playtime.injector.Component;

import java.util.function.Function;

@FunctionalInterface
public interface PriorityProvider extends Function<Component<?>, Priority> {
}

