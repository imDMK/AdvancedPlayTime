package com.github.imdmk.spenttime.platform.gui.render;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Strategy interface for checking player permissions.
 * <p>
 * This abstraction allows GUIs and renderers to remain independent
 * from the underlying permission system (e.g. Bukkit, Vault, LuckPerms).
 * <p>
 * Implementations should be thread-safe if evaluated asynchronously.
 */
@FunctionalInterface
public interface PermissionEvaluator {

    /**
     * Checks whether the given human entity possesses the specified permission.
     *
     * @param entity      the entity being checked (non-null)
     * @param permission  the permission node (non-null)
     * @return {@code true} if the player has the permission; {@code false} otherwise
     */
    boolean has(@NotNull HumanEntity entity, @NotNull String permission);
}
