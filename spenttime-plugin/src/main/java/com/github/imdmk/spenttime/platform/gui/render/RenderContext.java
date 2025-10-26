package com.github.imdmk.spenttime.platform.gui.render;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable context used during GUI item rendering.
 * <p>
 * Encapsulates the viewer and the permission evaluation strategy,
 * ensuring renderers remain stateless and easily testable.
 * <p>
 * <strong>Thread-safety:</strong> This record is immutable and thread-safe
 * as long as the underlying {@link PermissionEvaluator} implementation is thread-safe.
 *
 * @param viewer              the player for whom the GUI is being rendered
 * @param permissionEvaluator the strategy used to check permissions
 */
public record RenderContext(
        @NotNull Player viewer,
        @NotNull PermissionEvaluator permissionEvaluator
) { }
