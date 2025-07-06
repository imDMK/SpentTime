package com.github.imdmk.spenttime;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.spenttime.configuration.ConfigurationManager;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.database.DatabaseConfiguration;
import com.github.imdmk.spenttime.database.DatabaseService;
import com.github.imdmk.spenttime.infrastructure.BukkitSpentTime;
import com.github.imdmk.spenttime.infrastructure.BukkitTaskScheduler;
import com.github.imdmk.spenttime.infrastructure.command.builder.handler.MissingPermissionHandler;
import com.github.imdmk.spenttime.infrastructure.command.builder.handler.UsageHandler;
import com.github.imdmk.spenttime.infrastructure.command.builder.player.PlayerArgument;
import com.github.imdmk.spenttime.infrastructure.command.builder.player.PlayerContextual;
import com.github.imdmk.spenttime.infrastructure.command.configurator.CommandConfiguration;
import com.github.imdmk.spenttime.infrastructure.command.configurator.CommandConfigurator;
import com.github.imdmk.spenttime.infrastructure.gui.GuiManager;
import com.github.imdmk.spenttime.infrastructure.gui.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.infrastructure.gui.implementation.ConfirmGui;
import com.github.imdmk.spenttime.infrastructure.message.MessageConfiguration;
import com.github.imdmk.spenttime.infrastructure.message.MessageResultHandler;
import com.github.imdmk.spenttime.infrastructure.message.MessageService;
import com.github.imdmk.spenttime.infrastructure.placeholder.PlaceholderRegistry;
import com.github.imdmk.spenttime.infrastructure.update.UpdateController;
import com.github.imdmk.spenttime.infrastructure.update.UpdateService;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserArgument;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.controller.UserCreateController;
import com.github.imdmk.spenttime.user.controller.UserSaveController;
import com.github.imdmk.spenttime.user.controller.UserUpdateController;
import com.github.imdmk.spenttime.user.feature.command.MigrateCommand;
import com.github.imdmk.spenttime.user.feature.command.ReloadCommand;
import com.github.imdmk.spenttime.user.feature.command.ResetAllCommand;
import com.github.imdmk.spenttime.user.feature.command.ResetCommand;
import com.github.imdmk.spenttime.user.feature.command.SetCommand;
import com.github.imdmk.spenttime.user.feature.command.TimeCommand;
import com.github.imdmk.spenttime.user.feature.command.TopCommand;
import com.github.imdmk.spenttime.user.feature.gui.SpentTimeTopGui;
import com.github.imdmk.spenttime.user.feature.migration.MigrationManager;
import com.github.imdmk.spenttime.user.feature.migration.MigrationTaskFactory;
import com.github.imdmk.spenttime.user.feature.placeholder.SpentTimePlaceholder;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.user.repository.impl.DaoUserRepository;
import com.github.imdmk.spenttime.user.repository.impl.EmptyUserRepository;
import com.github.imdmk.spenttime.user.task.UserSaveTask;
import com.github.imdmk.spenttime.util.DurationUtil;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.triumphteam.gui.guis.BaseGui;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Core plugin class responsible for initializing and managing the SpentTime plugin lifecycle.
 * <p>
 * It handles configuration loading, database connections, command registration,
 * event listeners, GUI setup, and integrates with PlaceholderAPI and metrics.
 */
class SpentTime implements SpentTimeApi {

    private final Server server;
    private final Logger logger;

    private final ConfigurationManager configurationManager;

    private final DatabaseService databaseService;

    private final UserCache userCache;
    private UserRepository userRepository;

    private final MessageService messageService;

    private final LiteCommands<CommandSender> liteCommands;

    private PlaceholderRegistry placeholderRegistry;

    private final Metrics metrics;

    /**
     * Constructs and initializes the SpentTime plugin internals.
     *
     * @param plugin the main Bukkit plugin instance
     */
    SpentTime(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        SpentTimeApiProvider.register(this);

        Stopwatch stopwatch = Stopwatch.createStarted();
        this.server = plugin.getServer();
        this.logger = plugin.getLogger();

        /* Configuration */
        this.configurationManager = new ConfigurationManager(this.logger, plugin.getDataFolder());

        PluginConfiguration pluginConfiguration = this.configurationManager.create(PluginConfiguration.class);
        DatabaseConfiguration databaseConfiguration = this.configurationManager.create(DatabaseConfiguration.class);
        MessageConfiguration messageConfiguration = this.configurationManager.create(MessageConfiguration.class);
        GuiConfiguration guiConfiguration = this.configurationManager.create(GuiConfiguration.class);
        CommandConfiguration commandConfiguration = this.configurationManager.create(CommandConfiguration.class);

        /* Database and user repository */
        this.databaseService = new DatabaseService(this.logger, plugin.getDataFolder(), databaseConfiguration);
        this.userCache = new UserCache();

        try {
            this.databaseService.connect();
            this.userRepository = new DaoUserRepository(this.databaseService.getConnectionSource(), this.userCache);
        }
        catch (SQLException sqlException) {
            this.userRepository = new EmptyUserRepository();
            this.logger.log(Level.SEVERE, "An error occurred while initializing the database. " +
                    "The plugin will run, but some functions may not work properly.", sqlException);
        }

        /* Services */
        BukkitSpentTime bukkitSpentTime = new BukkitSpentTime(this.server);

        UserService userService = new UserService(this.logger, this.userRepository, this.userCache, bukkitSpentTime);
        UpdateService updateService = new UpdateService(pluginConfiguration, plugin.getDescription());
        this.messageService = new MessageService(messageConfiguration, BukkitAudiences.create(plugin), MiniMessage.miniMessage());

        /* Task scheduler */
        TaskScheduler taskScheduler = new BukkitTaskScheduler(plugin, this.server);

        /* Tasks */
        taskScheduler.runTimerAsync(new UserSaveTask(this.server, this.userCache, userService), DurationUtil.toTicks(Duration.ofMinutes(1)), DurationUtil.toTicks(pluginConfiguration.spentTimeSaveDelay));

        /* Migration */
        MigrationManager migrationManager = new MigrationManager(taskScheduler);
        MigrationTaskFactory migrationTaskFactory = new MigrationTaskFactory(this.logger, this.messageService, userService, migrationManager);

        /* Guis */
        GuiManager guiManager = new GuiManager(taskScheduler);
        Stream.of(
                new ConfirmGui(guiConfiguration, taskScheduler),
                new SpentTimeTopGui(this.logger, this.server, guiConfiguration, userService, this.userRepository, this.messageService, guiManager, taskScheduler)
        ).forEach(guiManager::registerGui);

        /* Controllers */
        Stream.of(
                new UserCreateController(this.logger, this.server, userService),
                new UserSaveController(this.userCache, userService),
                new UserUpdateController(this.userCache, userService),
                new UpdateController(this.logger, pluginConfiguration, this.messageService, updateService, taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* LiteCommands */
        this.liteCommands = LiteBukkitFactory.builder("SpentTime", plugin, this.server)

                .context(Player.class, new PlayerContextual())

                .argument(Player.class, new PlayerArgument(this.server, messageConfiguration))
                .argument(User.class, new UserArgument(this.userCache, this.userRepository, messageConfiguration))

                .result(Notice.class, new MessageResultHandler(this.messageService))
                .missingPermission(new MissingPermissionHandler(this.messageService))
                .invalidUsage(new UsageHandler(this.messageService))

                .commands(
                        new TimeCommand(this.logger, userService, this.messageService, bukkitSpentTime),
                        new TopCommand(this.logger, pluginConfiguration, this.userRepository, this.messageService, guiManager),

                        new SetCommand(this.logger, userService, this.messageService, bukkitSpentTime),
                        new ResetCommand(this.logger, userService, this.messageService, guiManager),
                        new ResetAllCommand(this.logger, this.messageService, this.userRepository, bukkitSpentTime, guiManager),
                        new ReloadCommand(this.logger, this.configurationManager, this.messageService),
                        new MigrateCommand(this.messageService, bukkitSpentTime, migrationManager, migrationTaskFactory)
                )

                .editorGlobal(new CommandConfigurator(this.logger, commandConfiguration))

                .build();

        /* PlaceholderAPI */
        if (this.server.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderRegistry = new PlaceholderRegistry();

            Stream.of(
                    new SpentTimePlaceholder(plugin.getDescription(), bukkitSpentTime)
            ).forEach(this.placeholderRegistry::register);
        }

        /* Metrics */
        this.metrics = new Metrics(plugin, SpentTimePlugin.METRICS_SERVICE_ID);

        this.logger.info("Enabled plugin in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms.");
    }

    /**
     * Disables the plugin, saving all data and unregistering resources.
     */
    void disable() {
        SpentTimeApiProvider.unregister();

        for (Player player : this.server.getOnlinePlayers()) {
            this.closeGui(player);
            this.saveUser(player);
        }

        this.configurationManager.shutdown();
        this.databaseService.close();
        this.messageService.close();
        this.liteCommands.unregister();

        if (this.placeholderRegistry != null) {
            this.placeholderRegistry.unregisterAll();
        }

        this.metrics.shutdown();

        this.logger.info("Successfully disabled plugin.");
    }

    /**
     * Closes any custom GUI opened by the player if present.
     *
     * @param player the player whose GUI should be closed
     */
    void closeGui(@NotNull Player player) {
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        if (topInventory.getHolder() instanceof BaseGui) {
            player.closeInventory();
        }
    }

    /**
     * Saves the user data of a player if present in the user cache.
     *
     * @param player the player whose data should be saved
     */
    void saveUser(@NotNull Player player) {
        this.userCache.getUserByUuid(player.getUniqueId())
                .ifPresent(user -> this.userRepository.save(user)
                        .exceptionally(throwable -> {
                            this.logger.log(Level.SEVERE, "Failed to save user data", throwable);
                            return null;
                        }));
    }

    @Override
    public @NotNull UserCache getUserCache() {
        return this.userCache;
    }

    @Override
    public @NotNull UserRepository getUserRepository() {
        return this.userRepository;
    }
}
