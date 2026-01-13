package com.github.imdmk.playtime.platform.gui.render;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PermissionEvaluator {

    boolean has(@NotNull HumanEntity entity, @NotNull String permission);
}
