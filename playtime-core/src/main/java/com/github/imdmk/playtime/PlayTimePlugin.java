package com.github.imdmk.playtime;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.playtime.infrastructure.database.DatabaseConfig;
import com.github.imdmk.playtime.infrastructure.database.DatabaseConnector;
import com.github.imdmk.playtime.infrastructure.database.driver.dependency.DriverDependencyLoader;
import com.github.imdmk.playtime.infrastructure.database.repository.RepositoryContext;
import com.github.imdmk.playtime.infrastructure.database.repository.RepositoryManager;
import com.github.imdmk.playtime.infrastructure.di.BindCore;
import com.github.imdmk.playtime.infrastructure.module.Module;
import com.github.imdmk.playtime.infrastructure.module.ModuleContext;
import com.github.imdmk.playtime.infrastructure.module.ModuleInitializer;
import com.github.imdmk.playtime.infrastructure.module.ModuleRegistry;
import com.github.imdmk.playtime.platform.events.BukkitEventCaller;
import com.github.imdmk.playtime.platform.events.BukkitListenerRegistrar;
import com.github.imdmk.playtime.platform.gui.GuiRegistry;
import com.github.imdmk.playtime.platform.litecommands.InvalidUsageHandlerImpl;
import com.github.imdmk.playtime.platform.litecommands.MissingPermissionsHandlerImpl;
import com.github.imdmk.playtime.platform.litecommands.NoticeResultHandlerImpl;
import com.github.imdmk.playtime.platform.litecommands.configurer.BukkitLiteCommandsRegistrar;
import com.github.imdmk.playtime.platform.litecommands.configurer.LiteCommandsRegistrar;
import com.github.imdmk.playtime.platform.logger.BukkitPluginLogger;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.placeholder.adapter.PlaceholderAdapter;
import com.github.imdmk.playtime.platform.placeholder.adapter.PlaceholderAdapterFactory;
import com.github.imdmk.playtime.platform.scheduler.BukkitTaskScheduler;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.shared.Validator;
import com.github.imdmk.playtime.shared.config.ConfigBinder;
import com.github.imdmk.playtime.shared.config.ConfigManager;
import com.github.imdmk.playtime.shared.config.ConfigSection;
import com.github.imdmk.playtime.shared.config.PluginConfig;
import com.github.imdmk.playtime.shared.message.MessageConfig;
import com.github.imdmk.playtime.shared.message.MessageService;
import com.github.imdmk.playtime.shared.time.Durations;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Main runtime bootstrap for the PlayTime plugin.
 * Threading note: heavy I/O offloaded to {@link ExecutorService}.
 */
final class PlayTimePlugin {

    private static final String PREFIX = "PlayTime";
    private static final int PLUGIN_METRICS_ID = 19362;

    @BindCore private final ModuleRegistry moduleRegistry = new ModuleRegistry();

    @BindCore private final Plugin plugin;
    @BindCore private final PluginLogger logger;
    @BindCore private final Server server;
    @BindCore private final ExecutorService executor;

    @BindCore private ConfigManager configManager;

    @BindCore private DatabaseConnector databaseConnector;
    @BindCore private RepositoryContext repositoryContext;
    @BindCore private RepositoryManager repositoryManager;

    @BindCore private MessageService messageService;
    @BindCore private TaskScheduler taskScheduler;
    @BindCore private BukkitEventCaller eventCaller;
    @BindCore private BukkitListenerRegistrar listenerRegistrar;
    @BindCore private GuiRegistry guiRegistry;
    @BindCore private PlaceholderAdapter placeholderAdapter;

    @BindCore private LiteCommandsRegistrar LiteCommandsRegistrar;
    private LiteCommands<CommandSender> liteCommands;

    private Metrics metrics;

    Injector injector;

    PlayTimePlugin(
            @NotNull Plugin plugin,
            @NotNull Server server,
            @NotNull PluginLogger logger,
            @NotNull ExecutorService executor) {
        this.plugin = Validator.notNull(plugin, "plugin cannot be null");
        this.server = Validator.notNull(server, "server cannot be null");
        this.logger = Validator.notNull(logger, "logger cannot be null");
        this.executor = Validator.notNull(executor, "executorService cannot be null");
    }

    PlayTimePlugin(@NotNull Plugin plugin, @NotNull ExecutorService executor) {
        this(plugin, plugin.getServer(), new BukkitPluginLogger(plugin), executor);
    }

    void enable(
            @NotNull List<Class<? extends ConfigSection>> enabledConfigs,
            @NotNull List<Class<? extends Module>> enabledModules) {
        Validator.notNull(enabledConfigs, "enabledConfigs cannot be null");
        Validator.notNull(enabledModules, "enabled modules cannot be null");

        final Stopwatch stopwatch = Stopwatch.createStarted();

        // Configuration
        configManager = new ConfigManager(logger, executor, plugin.getDataFolder());
        configManager.createAll(enabledConfigs);

        // Duration format style
        final PluginConfig pluginConfig = configManager.require(PluginConfig.class);
        Durations.setDefaultFormatStyle(pluginConfig.durationFormatStyle);

        // Database
        final DatabaseConfig databaseConfig = configManager.require(DatabaseConfig.class);

        final DriverDependencyLoader databaseDependencyLoader = new DriverDependencyLoader(plugin);
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
        repositoryContext = new RepositoryContext(executor);
        repositoryManager = new RepositoryManager(logger);

        final MessageConfig messageConfig = configManager.require(MessageConfig.class);
        messageService = new MessageService(messageConfig, BukkitAudiences.create(plugin));

        taskScheduler = new BukkitTaskScheduler(plugin, server.getScheduler());
        eventCaller = new BukkitEventCaller(server, taskScheduler);
        listenerRegistrar = new BukkitListenerRegistrar(plugin);
        guiRegistry = new GuiRegistry();
        placeholderAdapter = PlaceholderAdapterFactory.createFor(plugin, server, logger);

        LiteCommandsRegistrar = new BukkitLiteCommandsRegistrar();
        LiteCommandsRegistrar.configure(builder -> {
            builder.invalidUsage(new InvalidUsageHandlerImpl(messageService));
            builder.missingPermission(new MissingPermissionsHandlerImpl(messageService));
            builder.result(Notice.class, new NoticeResultHandlerImpl(messageService));
        });

        // Dependency Injection
        injector = DependencyInjection.createInjector(resources -> {
            new PlayTimeCoreBinder(this).bind(resources);
            ConfigBinder.bind(resources, configManager.getConfigs());
        });

        // Module initialization
        final ModuleContext context = injector.newInstance(ModuleContext.class);
        final ModuleInitializer initializer = new ModuleInitializer(context, moduleRegistry, injector);

        initializer.loadAndSort(enabledModules);
        initializer.bindAll();
        initializer.initAll();
        initializer.registerRepositories();

        // Start repositories
        Validator.ifNotNull(databaseConnector.getConnectionSource(), connection -> {
            try {
                repositoryManager.startAll(connection);
            } catch (SQLException e) {
                logger.error(e, "An error occurred while trying to start all repositories");
                throw new IllegalStateException("Repository startup failed", e);
            }
        });

        // Activate all feature modules
        initializer.activateFeatures();

        // Build and register commands
        liteCommands = LiteCommandsRegistrar.create(PREFIX, plugin, server);

        // Metrics
        metrics = new Metrics(plugin, PLUGIN_METRICS_ID);

        // API
        final PlayTimeApiAdapter api = injector.newInstance(PlayTimeApiAdapter.class);
        PlayTimeApiProvider.register(api);

        final Duration elapsed = stopwatch.stop().elapsed();
        logger.info("AdvancedPlaytime plugin enabled in %s ms", elapsed.toMillis());
    }

    void disable() {
        Validator.ifNotNull(configManager, (manager) -> {
            manager.saveAllSync();
            manager.shutdown();
        });
        Validator.ifNotNull(repositoryManager, RepositoryManager::close);
        Validator.ifNotNull(databaseConnector, DatabaseConnector::close);
        Validator.ifNotNull(messageService, MessageService::shutdown);
        Validator.ifNotNull(taskScheduler, TaskScheduler::shutdown);
        Validator.ifNotNull(liteCommands, LiteCommands::unregister);
        Validator.ifNotNull(metrics, Metrics::shutdown);

        PlayTimeApiProvider.unregister();

        logger.info("PlayTime plugin disabled successfully.");
    }
}
