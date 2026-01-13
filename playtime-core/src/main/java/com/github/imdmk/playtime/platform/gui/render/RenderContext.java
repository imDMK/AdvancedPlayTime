package com.github.imdmk.playtime.platform.gui.render;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public record RenderContext(
        @NotNull Player viewer,
        @NotNull PermissionEvaluator permissionEvaluator
) {

    public static RenderContext defaultContext(@NotNull Player viewer) {
        return new RenderContext(viewer, Permissible::hasPermission);
    }

}
