package com.github.imdmk.spenttime;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.spenttime.infrastructure.database.DatabaseConfig;
import com.github.imdmk.spenttime.infrastructure.database.DatabaseConnector;
import com.github.imdmk.spenttime.infrastructure.database.dependency.DatabaseDependencyLoader;
import com.github.imdmk.spenttime.infrastructure.database.repository.RepositoryContext;
import com.github.imdmk.spenttime.infrastructure.database.repository.RepositoryManager;
import com.github.imdmk.spenttime.infrastructure.module.PluginModule;
import com.github.imdmk.spenttime.infrastructure.module.PluginModuleInitializer;
import com.github.imdmk.spenttime.infrastructure.module.PluginModuleRegistry;
import com.github.imdmk.spenttime.platform.events.BukkitEventCaller;
import com.github.imdmk.spenttime.platform.events.BukkitListenerRegistrar;
import com.github.imdmk.spenttime.platform.gui.GuiRegistry;
import com.github.imdmk.spenttime.platform.litecommands.InvalidUsageHandlerImpl;
import com.github.imdmk.spenttime.platform.litecommands.MissingPermissionsHandlerImpl;
import com.github.imdmk.spenttime.platform.litecommands.NoticeResultHandlerImpl;
import com.github.imdmk.spenttime.platform.litecommands.configurer.BukkitLiteCommandsConfigurer;
import com.github.imdmk.spenttime.platform.litecommands.configurer.LiteCommandsConfigurer;
import com.github.imdmk.spenttime.platform.logger.BukkitPluginLogger;
import com.github.imdmk.spenttime.platform.logger.PluginLogger;
import com.github.imdmk.spenttime.platform.scheduler.BukkitTaskScheduler;
import com.github.imdmk.spenttime.platform.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.shared.Validator;
import com.github.imdmk.spenttime.shared.config.ConfigBinder;
import com.github.imdmk.spenttime.shared.config.ConfigManager;
import com.github.imdmk.spenttime.shared.config.ConfigSection;
import com.github.imdmk.spenttime.shared.config.PluginConfig;
import com.github.imdmk.spenttime.shared.message.MessageConfig;
import com.github.imdmk.spenttime.shared.message.MessageService;
import com.github.imdmk.spenttime.shared.time.Durations;
import com.google.common.base.Stopwatch;
import com.j256.ormlite.support.ConnectionSource;
import dev.rollczi.litecommands.LiteCommands;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Main runtime bootstrap for the SpentTime plugin.
 * Threading note: heavy I/O offloaded to {@link ExecutorService}.
 */
final class SpentTimePlugin {

    private static final String PREFIX = "SpentTime";

    private final Plugin plugin;
    private final PluginLogger logger;
    private final Server server;
    private final ExecutorService executorService;

    private ConfigManager configManager;

    private DatabaseConnector databaseConnector;
    private RepositoryContext repositoryContext;
    private RepositoryManager repositoryManager;

    private MessageService messageService;
    private TaskScheduler taskScheduler;
    private BukkitEventCaller eventCaller;
    private BukkitListenerRegistrar listenerRegistrar;

    private GuiRegistry guiRegistry;

    private LiteCommandsConfigurer liteCommandsConfigurer;
    private LiteCommands<CommandSender> liteCommands;

    private Injector injector;

    SpentTimePlugin(
            @NotNull Plugin plugin,
            @NotNull Server server,
            @NotNull PluginLogger logger,
            @NotNull ExecutorService executorService) {
        this.plugin = Validator.notNull(plugin, "plugin cannot be null");
        this.server = Validator.notNull(server, "server cannot be null");
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.executorService = Validator.notNull(executorService, "executorService cannot be null");
    }

    SpentTimePlugin(@NotNull final Plugin plugin, @NotNull final ExecutorService executorService) {
        this(plugin, plugin.getServer(), new BukkitPluginLogger(plugin), executorService);
    }

    /**
     * Enable plugin with a given set of modules.
     *
     * @param enabledConfigs config catalog to create/load
     * @param enabledModules list of module classes to load (order resolved by manager)
     */
    void enable(
            @NotNull final List<Class<? extends ConfigSection>> enabledConfigs,
            @NotNull final List<Class<? extends PluginModule>> enabledModules) {
        Validator.notNull(enabledConfigs, "enabledConfigs cannot be null");
        Validator.notNull(enabledModules, "enabled modules cannot be null");

        final Stopwatch stopwatch = Stopwatch.createStarted();

        // Configuration
        configManager = new ConfigManager(logger, executorService, plugin.getDataFolder());
        configManager.createAll(enabledConfigs);

        // Duration format style
        final PluginConfig pluginConfig = configManager.require(PluginConfig.class);
        Durations.setDefaultFormatStyle(pluginConfig.durationFormatStyle);

        // Database connection
        final DatabaseConfig databaseConfig = configManager.require(DatabaseConfig.class);

        final DatabaseDependencyLoader databaseDependencyLoader = new DatabaseDependencyLoader(plugin);

        logger.info("Resolving database driver for database mode: " + databaseConfig.databaseMode);
        databaseDependencyLoader.loadDriverFor(databaseConfig.databaseMode);

        logger.info("Connecting to database...");
        databaseConnector = new DatabaseConnector(logger, databaseConfig);
        try {
            databaseConnector.connect(plugin.getDataFolder());
        } catch (SQLException e) {
            logger.error(e, "Failed to connect database.");
            throw new IllegalStateException("Database initialization failed", e);
        }

        // Infrastructure services
        repositoryContext = new RepositoryContext(executorService);
        repositoryManager = new RepositoryManager(logger);

        final MessageConfig messageConfig = configManager.require(MessageConfig.class);
        messageService = new MessageService(messageConfig, BukkitAudiences.create(plugin));

        taskScheduler = new BukkitTaskScheduler(plugin, server.getScheduler());
        eventCaller = new BukkitEventCaller(server, taskScheduler);
        listenerRegistrar = new BukkitListenerRegistrar(plugin);
        guiRegistry = new GuiRegistry();

        liteCommandsConfigurer = new BukkitLiteCommandsConfigurer();
        liteCommandsConfigurer.configure(builder -> {
            builder.invalidUsage(new InvalidUsageHandlerImpl(messageService));
            builder.missingPermission(new MissingPermissionsHandlerImpl(messageService));
            builder.result(Notice.class, new NoticeResultHandlerImpl(messageService));
        });

        // Dependency Injection
        injector = DependencyInjection.createInjector(this::bindCore);

        // Module initialization
        final PluginModuleInitializer initializer = this.injector.newInstance(PluginModuleInitializer.class);

        initializer.loadAndSort(enabledModules);
        initializer.bindAll();
        initializer.initAll();
        initializer.registerRepositories();

        // Start repositories
        try {
            final ConnectionSource connection = databaseConnector.getConnectionSource();
            if (connection != null) {
                repositoryManager.startAll(connection);
            }
        } catch (SQLException e) {
            logger.error(e, "An error occurred while trying to start all repositories");
            throw new IllegalStateException("Repository startup failed", e);
        }

        // Activate all feature modules
        initializer.activateFeatures();

        // Build and register commands
        liteCommands = liteCommandsConfigurer.create(PREFIX, plugin, server);

        // API
        final SpentTimeApiAdapter api = injector.newInstance(SpentTimeApiAdapter.class);
        SpentTimeApiProvider.register(api);

        final Duration elapsed = stopwatch.stop().elapsed();
        logger.info("SpentTime enabled in %s ms", elapsed.toMillis());
    }


    /**
     * Disable plugin and release resources in a safe order.
     */
    void disable() {
        SpentTimeApiProvider.unregister();

        Validator.ifNotNull(taskScheduler, TaskScheduler::shutdown);
        Validator.ifNotNull(liteCommands, LiteCommands::unregister);
        Validator.ifNotNull(repositoryManager, RepositoryManager::close);
        Validator.ifNotNull(databaseConnector, DatabaseConnector::close);
        Validator.ifNotNull(messageService, MessageService::shutdown);
        Validator.ifNotNull(configManager, (manager) -> {
            manager.saveAllSync();
            manager.shutdown();
        });

        logger.info("SpentTime plugin disabled successfully.");
    }

    /**
     * Bind core singletons into DI container.
     */
    private void bindCore(@NotNull Resources resources) {
        Validator.notNull(resources, "resources cannot be null");

        resources.on(Plugin.class).assignInstance(plugin);
        resources.on(PluginLogger.class).assignInstance(logger);
        resources.on(Server.class).assignInstance(server);
        resources.on(ExecutorService.class).assignInstance(executorService);

        resources.on(ConfigManager.class).assignInstance(configManager);
        ConfigBinder.bind(resources, configManager.getConfigs());

        resources.on(RepositoryContext.class).assignInstance(repositoryContext);
        resources.on(RepositoryManager.class).assignInstance(repositoryManager);

        resources.on(MessageService.class).assignInstance(messageService);
        resources.on(TaskScheduler.class).assignInstance(taskScheduler);
        resources.on(BukkitEventCaller.class).assignInstance(eventCaller);
        resources.on(BukkitListenerRegistrar.class).assignInstance(listenerRegistrar);
        resources.on(GuiRegistry.class).assignInstance(guiRegistry);

        resources.on(LiteCommandsConfigurer.class).assignInstance(liteCommandsConfigurer);
        resources.on(LiteCommands.class).assignInstance(liteCommands);

        resources.on(PluginModuleRegistry.class).assignInstance(new PluginModuleRegistry());
        resources.on(Injector.class).assignInstance(() -> injector);
    }
}
