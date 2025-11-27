package com.github.imdmk.playtime.infrastructure.module;

/**
 * Defines a simple ordering contract for modules.
 * Lower values indicate higher priority (executed earlier).
 *
 * <p>Ordering is used by the module manager to produce a deterministic
 * initialization sequence. When two modules return the same value, the
 * manager should apply a stable tie-breaker (e.g., class name).</p>
 */
interface Ordered {

    /**
     * Returns the order value of this component.
     * Lower values mean earlier execution.
     *
     * @return the order value (may be negative)
     */
    int order();
}
