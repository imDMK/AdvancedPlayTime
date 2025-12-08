package com.github.imdmk.playtime;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.playtime.infrastructure.database.DatabaseConfig;
import com.github.imdmk.playtime.infrastructure.database.DatabaseManager;
import com.github.imdmk.playtime.infrastructure.database.repository.RepositoryContext;
import com.github.imdmk.playtime.infrastructure.database.repository.RepositoryManager;
import com.github.imdmk.playtime.infrastructure.injector.Bind;
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
import com.github.imdmk.playtime.platform.logger.BukkitPluginLogger;
import com.github.imdmk.playtime.platform.logger.PluginLogger;
import com.github.imdmk.playtime.platform.placeholder.adapter.PlaceholderAdapter;
import com.github.imdmk.playtime.platform.placeholder.adapter.PlaceholderAdapterFactory;
import com.github.imdmk.playtime.platform.scheduler.BukkitTaskScheduler;
import com.github.imdmk.playtime.platform.scheduler.TaskScheduler;
import com.github.imdmk.playtime.shared.validate.Validator;
import com.github.imdmk.playtime.config.ConfigManager;
import com.github.imdmk.playtime.config.ConfigSection;
import com.github.imdmk.playtime.config.InjectorConfigBinder;
import com.github.imdmk.playtime.config.PluginConfig;
import com.github.imdmk.playtime.message.MessageConfig;
import com.github.imdmk.playtime.message.MessageService;
import com.github.imdmk.playtime.shared.time.Durations;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
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

    private static final String PREFIX = "AdvancedPlayTime";
    private static final int PLUGIN_METRICS_ID = 19362;

    @Bind private final ModuleRegistry moduleRegistry = new ModuleRegistry();

    @Bind private final Plugin plugin;
    @Bind private final PluginLogger logger;
    @Bind private final Server server;
    @Bind private final ExecutorService executor;

    @Bind private ConfigManager configManager;

    @Bind private DatabaseManager databaseManager;
    @Bind private RepositoryContext repositoryContext;
    @Bind private RepositoryManager repositoryManager;

    @Bind private MessageService messageService;
    @Bind private TaskScheduler taskScheduler;
    @Bind private BukkitEventCaller eventCaller;
    @Bind private BukkitListenerRegistrar listenerRegistrar;
    @Bind private GuiRegistry guiRegistry;
    @Bind private PlaceholderAdapter placeholderAdapter;

    @Bind private LiteCommandsBuilder<CommandSender, ?, ?> liteCommandsBuilder;
    @Bind private LiteCommands<?> liteCommands;

    private Metrics metrics;

    Injector injector;

    PlayTimePlugin(
            @NotNull Plugin plugin,
            @NotNull Server server,
            @NotNull PluginLogger logger,
            @NotNull ExecutorService executor
    ) {
        this.plugin = Validator.notNull(plugin, "plugin");
        this.server = Validator.notNull(server, "server");
        this.logger = Validator.notNull(logger, "logger");
        this.executor = Validator.notNull(executor, "executorService");
    }

    PlayTimePlugin(@NotNull Plugin plugin, @NotNull ExecutorService executor) {
        this(plugin, plugin.getServer(), new BukkitPluginLogger(plugin), executor);
    }

    void enable(
            @NotNull List<Class<? extends ConfigSection>> enabledConfigs,
            @NotNull List<Class<? extends Module>> enabledModules
    ) {
        Validator.notNull(enabledConfigs, "enabledConfigs");
        Validator.notNull(enabledModules, "enabled modules");

        final Stopwatch stopwatch = Stopwatch.createStarted();

        // Configuration
        configManager = new ConfigManager(logger, plugin.getDataFolder());
        configManager.createAll(enabledConfigs);

        // Duration format style
        final PluginConfig pluginConfig = configManager.require(PluginConfig.class);
        Durations.setDefaultFormatStyle(pluginConfig.durationFormatStyle);

        // Database
        final DatabaseConfig databaseConfig = configManager.require(DatabaseConfig.class);
        databaseManager = new DatabaseManager(plugin, logger, databaseConfig);

        databaseManager.loadDriver();
        try {
            databaseManager.connect();
        } catch (SQLException e) {
            logger.error(e, "An error occurred while trying to start all repositories. Disabling plugin...");
            plugin.getPluginLoader().disablePlugin(plugin);
            throw new IllegalStateException("Repository startup failed", e);
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

        liteCommandsBuilder = LiteBukkitFactory.builder(PREFIX, plugin, server);
        liteCommandsBuilder
                .invalidUsage(new InvalidUsageHandlerImpl(messageService))
                .missingPermission(new MissingPermissionsHandlerImpl(messageService))
                .result(Notice.class, new NoticeResultHandlerImpl(messageService));

        // Dependency Injection
        injector = DependencyInjection.createInjector(resources -> {
            new PlayTimeBinder(this).bind(resources);
            InjectorConfigBinder.bind(resources, configManager.getConfigs());
        });

        // Module initialization
        final ModuleContext context = injector.newInstance(ModuleContext.class);
        final ModuleInitializer initializer = new ModuleInitializer(context, moduleRegistry, injector);

        initializer.loadAndSort(enabledModules);
        initializer.bindAll();
        initializer.initAll();
        initializer.registerRepositories();

        // Start repositories
        Validator.ifNotNull(databaseManager.getConnection(), connection -> {
            try {
                repositoryManager.startAll(connection);
            } catch (SQLException e) {
                logger.error(e, "An error occurred while trying to start all repositories. Disabling plugin...");
                plugin.getPluginLoader().disablePlugin(plugin);
                throw new IllegalStateException("Repository startup failed", e);
            }
        });

        // Activate all feature modules
        initializer.activateFeatures();

        // Build commands
        liteCommands = liteCommandsBuilder.build();

        // Metrics
        metrics = new Metrics(plugin, PLUGIN_METRICS_ID);

        // API
        final PlayTimeApiAdapter api = injector.newInstance(PlayTimeApiAdapter.class);
        PlayTimeApiProvider.register(api);

        final Duration elapsed = stopwatch.stop().elapsed();
        logger.info("%s plugin enabled in %s ms", PREFIX, elapsed.toMillis());
    }

    void disable() {
        Validator.ifNotNull(configManager, (manager) -> {
            manager.saveAll();
            manager.clearAll();
        });
        Validator.ifNotNull(repositoryManager, RepositoryManager::close);
        Validator.ifNotNull(databaseManager, DatabaseManager::shutdown);
        Validator.ifNotNull(messageService, MessageService::shutdown);
        Validator.ifNotNull(taskScheduler, TaskScheduler::shutdown);
        Validator.ifNotNull(liteCommands, LiteCommands::unregister);
        Validator.ifNotNull(metrics, Metrics::shutdown);

        PlayTimeApiProvider.unregister();

        logger.info("%s plugin disabled successfully.", PREFIX);
    }
}
