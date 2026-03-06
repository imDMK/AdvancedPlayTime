package com.github.imdmk.playtime.core.platform.gui.render;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;


public record RenderContext(
        Player viewer,
        PermissionEvaluator permissionEvaluator
) {

    public static RenderContext defaultContext(Player viewer) {
        return new RenderContext(viewer, Permissible::hasPermission);
    }

}
