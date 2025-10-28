package com.github.imdmk.spenttime.infrastructure.module.phase;

import com.github.imdmk.spenttime.infrastructure.database.ormlite.RepositoryManager;
import org.jetbrains.annotations.NotNull;

/**
 * Functional phase interface responsible for repository registration.
 * <p>
 * Implementations should declare repository descriptors only — no database I/O
 * should occur during this phase.
 */
@FunctionalInterface
public interface RepositoryPhase {

    /**
     * Registers repository descriptors into the {@link RepositoryManager}.
     *
     * @param manager the repository manager used for descriptor registration (never {@code null})
     */
    void register(@NotNull RepositoryManager manager);
}
