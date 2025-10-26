package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.infrastructure.database.DatabaseConfig;
import com.github.imdmk.spenttime.infrastructure.database.DatabaseConnector;
import com.github.imdmk.spenttime.infrastructure.module.PluginModule;
import com.github.imdmk.spenttime.infrastructure.module.PluginModuleCore;
import com.github.imdmk.spenttime.infrastructure.module.PluginModuleInitializer;
import com.github.imdmk.spenttime.infrastructure.module.PluginModuleRegistry;
import com.github.imdmk.spenttime.infrastructure.ormlite.RepositoryManager;
import com.github.imdmk.spenttime.platform.events.BukkitEventCaller;
import com.github.imdmk.spenttime.platform.litecommands.BukkitLiteCommandsConfigurer;
import com.github.imdmk.spenttime.platform.litecommands.LiteCommandsConfigurer;
import com.github.imdmk.spenttime.platform.scheduler.BukkitTaskScheduler;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.shared.config.ConfigManager;
import com.github.imdmk.spenttime.shared.config.PluginConfig;
import com.github.imdmk.spenttime.shared.message.MessageConfig;
import com.github.imdmk.spenttime.shared.message.MessageService;
import com.google.common.base.Stopwatch;
import com.j256.ormlite.support.ConnectionSource;
import dev.rollczi.litecommands.LiteCommands;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Main runtime bootstrap for the SpentTime plugin.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>wire DI and core services,</li>
 *   <li>initialize modules (bind → init → repos → start → activate),</li>
 *   <li>build commands, expose public API,</li>
 *   <li>shutdown everything gracefully.</li>
 * </ul>
 * Threading note: heavy I/O offloaded to {@link ExecutorService}.
 */
final class SpentTime {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpentTime.class);
    private static final String PREFIX = "SpentTime";

    private final Plugin plugin;
    private final Server server;
    private final ExecutorService executorService;

    private ConfigManager configManager;
    private DatabaseConnector databaseConnector;
    private RepositoryManager repositoryManager;

    private BukkitAudiences audiences;
    private MessageService messageService;
    private BukkitEventCaller bukkitEventCaller;
    private TaskScheduler taskScheduler;

    private LiteCommandsConfigurer liteCommandsConfigurer;
    private LiteCommands<CommandSender> liteCommands;

    SpentTime(@NotNull final Plugin plugin, @NotNull final ExecutorService executorService) {
        Validator.notNull(plugin, "plugin cannot be null");
        Validator.notNull(executorService, "executorService cannot be null");

        this.plugin = plugin;
        this.server = plugin.getServer();
        this.executorService = executorService;
    }

    /**
     * Enable plugin with a given set of modules.
     *
     * @param enabledModules list of module classes to load (order resolved by manager)
     */
    void enable(@NotNull final List<Class<? extends PluginModule>> enabledModules) {
        Validator.notNull(enabledModules, "enabled modules cannot be null");

        LOGGER.info("Enabling SpentTime plugin...");
        final Stopwatch stopwatch = Stopwatch.createStarted();

        // Configuration
        this.configManager = new ConfigManager(this.executorService);
        final PluginConfig pluginConfig = this.configManager.create(PluginConfig.class);
        final MessageConfig messageConfig = this.configManager.create(MessageConfig.class);
        final DatabaseConfig databaseConfig = this.configManager.create(DatabaseConfig.class);

        // Database connection
        this.databaseConnector = new DatabaseConnector(databaseConfig);
        try {
            this.databaseConnector.connect(this.plugin.getDataFolder());
        } catch (SQLException e) {
            throw new IllegalStateException("Database initialization failed", e);
        }

        // Infrastructure services
        this.repositoryManager = new RepositoryManager();
        this.audiences = BukkitAudiences.create(this.plugin);
        this.messageService = new MessageService(messageConfig, this.audiences, MiniMessage.miniMessage());
        this.bukkitEventCaller = new BukkitEventCaller(this.server);
        this.taskScheduler = new BukkitTaskScheduler(this.plugin, this.server.getScheduler());
        this.liteCommandsConfigurer = new BukkitLiteCommandsConfigurer();

        // Dependency Injection
        final Injector injector = DependencyInjection.createInjector(this::bindCore);

        // Module initialization
        final PluginModuleInitializer moduleInitializer = injector.newInstanceWithFields(PluginModuleInitializer.class);

        moduleInitializer.loadAndSort(enabledModules);
        moduleInitializer.bindAll();
        moduleInitializer.initAll();
        moduleInitializer.registerRepositories();

        // Start repositories
        try {
            final ConnectionSource connection = this.databaseConnector.getConnectionSource();
            if (connection != null) {
                this.repositoryManager.startAll(connection);
            }
        } catch (SQLException e) {
            LOGGER.error("An error occurred while trying to start all repositories", e);
            throw new IllegalStateException("Repository startup failed", e);
        }

        // Activate all feature modules
        moduleInitializer.activateFeatures();

        // Build and register commands
        this.liteCommands = this.liteCommandsConfigurer.create(PREFIX, this.plugin, this.server);

        // API
        SpentTimeApiAdapter api = injector.newInstanceWithFields(SpentTimeApiAdapter.class);
        SpentTimeApiProvider.register(api);

        final Duration elapsed = stopwatch.stop().elapsed();
        LOGGER.info("SpentTime enabled in {} ms", elapsed.toMillis());
    }


    /**
     * Disable plugin and release resources in a safe order.
     */
    void disable() {
        SpentTimeApiProvider.unregister();

        this.taskScheduler.shutdown();
        this.liteCommands.unregister();

        this.repositoryManager.close();
        this.databaseConnector.close();
        this.audiences.close();

        this.configManager.saveAll();

        LOGGER.info("SpentTime plugin disabled successfully.");
    }

    /**
     * Bind core singletons into DI container.
     */
    private void bindCore(@NotNull Resources resources) {
        Validator.notNull(resources, "resources cannot be null");

        resources.on(Plugin.class).assignInstance(this.plugin);
        resources.on(Server.class).assignInstance(this.server);

        resources.on(ConfigManager.class).assignInstance(this.configManager);
        this.configManager.getConfigs().forEach(config -> resources.on(config.getClass()).assignInstance(config));

        resources.on(MessageService.class).assignInstance(this.messageService);
        resources.on(TaskScheduler.class).assignInstance(this.taskScheduler);
        resources.on(RepositoryManager.class).assignInstance(this.repositoryManager);
        resources.on(LiteCommandsConfigurer.class).assignInstance(this.liteCommandsConfigurer);
        resources.on(ExecutorService.class).assignInstance(this.executorService);
        resources.on(BukkitEventCaller.class).assignInstance(this.bukkitEventCaller);

        resources.on(PluginModuleRegistry.class).assignInstance(new PluginModuleRegistry());
    }
}
