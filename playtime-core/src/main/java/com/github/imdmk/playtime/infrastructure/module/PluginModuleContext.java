package com.github.imdmk.playtime.infrastructure.module;

import com.github.imdmk.playtime.infrastructure.database.repository.RepositoryManager;
import com.github.imdmk.playtime.platform.events.BukkitListenerRegistrar;
import com.github.imdmk.playtime.platform.gui.GuiRegistry;
import com.github.imdmk.playtime.platform.litecommands.configurer.LiteCommandsRegistrar;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.placeholder.adapter.PlaceholderAdapter;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.annotations.Inject;

/**
 * Immutable container holding all shared services exposed to {@link PluginModule} implementations.
 *
 * <p>Acts as a central context object passed to all module lifecycle phases, providing access to:
 * <ul>
 *     <li>Bukkit plugin and server environment,</li>
 *     <li>logging, scheduling and repository infrastructure,</li>
 *     <li>listener/command/GUI registrars,</li>
 *     <li>placeholder adapter (PlaceholderAPI-enabled or no-op).</li>
 * </ul>
 *
 * <p>This record is created once during plugin bootstrap and reused throughout the
 * module initialization pipeline.</p>
 */
@Inject
public record PluginModuleContext(
        @NotNull Plugin plugin,
        @NotNull Server server,
        @NotNull PluginLogger logger,
        @NotNull TaskScheduler taskScheduler,
        @NotNull RepositoryManager repositoryManager,
        @NotNull BukkitListenerRegistrar listenerRegistrar,
        @NotNull LiteCommandsRegistrar liteCommandsRegistrar,
        @NotNull GuiRegistry guiRegistry,
        @NotNull PlaceholderAdapter placeholderAdapter) {
}
