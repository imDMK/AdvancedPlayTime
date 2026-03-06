package com.github.imdmk.playtime.core.platform.gui.render;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public record RenderOptions(
        NoPermissionPolicy policy,
        Consumer<InventoryClickEvent> onDenied
) {

    public static RenderOptions defaultDenySilently() {
        return new RenderOptions(NoPermissionPolicy.DISABLE, e -> {});
    }

    public static RenderOptions defaultHide() {
        return new RenderOptions(NoPermissionPolicy.HIDE, e -> {});
    }
}
