package com.github.imdmk.playtime.infrastructure.module.phase;

import com.github.imdmk.playtime.platform.placeholder.adapter.PlaceholderAdapter;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PlaceholderPhase {

    void register(@NotNull PlaceholderAdapter placeholderAdapter);
}
