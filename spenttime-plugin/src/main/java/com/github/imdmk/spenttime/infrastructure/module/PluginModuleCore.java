package com.github.imdmk.spenttime.infrastructure.module;

import com.github.imdmk.spenttime.infrastructure.ormlite.RepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.Injector;

import java.util.function.Consumer;

/**
 * Core module – responsible for foundational services and repositories.
 *
 * <p>After {@link #bind} and {@link #init}, the manager will invoke
 * {@link #repositories(Injector)} and apply the returned {@link Consumer}
 * to the active {@link RepositoryManager}.</p>
 *
 * <p>Tip: keep heavy I/O out of {@code repositories()} – only register descriptors;
 * real DB work should happen when the {@link RepositoryManager} starts.</p>
 */
public interface PluginModuleCore extends PluginModule {

    /**
     * Provides a registration hook for repositories.
     *
     * @param injector DI injector (never {@code null})
     * @return a no-op consumer by default; override to register repositories
     */
    default Consumer<RepositoryManager> repositories(@NotNull Injector injector) {
        return manager -> {};
    }
}
