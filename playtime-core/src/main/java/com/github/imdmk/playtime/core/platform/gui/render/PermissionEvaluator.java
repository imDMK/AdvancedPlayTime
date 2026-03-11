package com.github.imdmk.playtime.core.platform.gui.render;

import org.bukkit.entity.HumanEntity;

@FunctionalInterface
public interface PermissionEvaluator {

    boolean has(HumanEntity entity, String permission);
}
