package com.github.imdmk.playtime.platform.gui.render;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record RenderOptions(
        @NotNull NoPermissionPolicy policy,
        @NotNull Consumer<InventoryClickEvent> onDenied
) {

    public static RenderOptions defaultDenySilently() {
        return new RenderOptions(NoPermissionPolicy.DISABLE, e -> {});
    }

    public static RenderOptions defaultHide() {
        return new RenderOptions(NoPermissionPolicy.HIDE, e -> {});
    }
}
