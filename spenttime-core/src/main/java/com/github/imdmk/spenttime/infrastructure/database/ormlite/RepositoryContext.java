package com.github.imdmk.spenttime.infrastructure.database.ormlite;

import com.github.imdmk.spenttime.shared.Validator;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.concurrent.ExecutorService;

/**
 * Provides a shared executor for asynchronous database operations.
 * <p>
 * This context ensures all DAO repositories use the same thread pool
 * (typically injected from the main scheduler).
 */
final class RepositoryContext {

    @Inject private ExecutorService dbExecutor;

    /** Returns the shared executor used for repository async operations. */
    @NotNull ExecutorService executor() {
        Validator.notNull(this.dbExecutor, "dbExecutor cannot be null");
        return this.dbExecutor;
    }
}
