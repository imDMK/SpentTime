package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.configuration.ConfigurationManager;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.database.DatabaseConfiguration;
import com.github.imdmk.spenttime.database.DatabaseService;
import com.github.imdmk.spenttime.feature.commands.builder.handler.MissingPermissionHandler;
import com.github.imdmk.spenttime.feature.commands.builder.handler.UsageHandler;
import com.github.imdmk.spenttime.feature.commands.builder.player.PlayerArgument;
import com.github.imdmk.spenttime.feature.commands.builder.player.PlayerContextual;
import com.github.imdmk.spenttime.feature.commands.implementation.ResetAllCommand;
import com.github.imdmk.spenttime.feature.commands.implementation.ResetCommand;
import com.github.imdmk.spenttime.feature.commands.implementation.SetCommand;
import com.github.imdmk.spenttime.feature.commands.implementation.TimeCommand;
import com.github.imdmk.spenttime.feature.commands.implementation.TopCommand;
import com.github.imdmk.spenttime.feature.gui.GuiManager;
import com.github.imdmk.spenttime.feature.gui.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.feature.gui.implementation.ConfirmationGui;
import com.github.imdmk.spenttime.feature.message.MessageConfiguration;
import com.github.imdmk.spenttime.feature.message.MessageService;
import com.github.imdmk.spenttime.feature.placeholder.PlaceholderRegistry;
import com.github.imdmk.spenttime.feature.placeholder.SpentTimePlaceholder;
import com.github.imdmk.spenttime.feature.update.UpdateController;
import com.github.imdmk.spenttime.feature.update.UpdateService;
import com.github.imdmk.spenttime.shared.BukkitTaskScheduler;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.user.BukkitSpentTime;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserArgument;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.controller.UserCreateController;
import com.github.imdmk.spenttime.user.controller.UserSaveController;
import com.github.imdmk.spenttime.user.controller.UserUpdateController;
import com.github.imdmk.spenttime.user.gui.SpentTimeTopGui;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.user.repository.impl.DaoUserRepositoryImpl;
import com.github.imdmk.spenttime.user.repository.impl.EmptyUserRepositoryImpl;
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
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

class SpentTime implements SpentTimeApi {

    private final Server server;
    private final Logger logger;

    private final DatabaseService databaseService;

    private final UserCache userCache;
    private UserRepository userRepository;

    private final MessageService messageService;

    private final LiteCommands<CommandSender> liteCommands;

    private PlaceholderRegistry placeholderRegistry;

    private final Metrics metrics;

    SpentTime(@NotNull Plugin plugin) {
        SpentTimeApiProvider.register(this);

        Stopwatch stopwatch = Stopwatch.createStarted();
        File dataFolder = plugin.getDataFolder();

        this.server = plugin.getServer();
        this.logger = plugin.getLogger();

        /* Configuration */
        ConfigurationManager configurationManager = new ConfigurationManager();

        PluginConfiguration pluginConfiguration = configurationManager.create(PluginConfiguration.class, dataFolder);
        DatabaseConfiguration databaseConfiguration = configurationManager.create(DatabaseConfiguration.class, dataFolder);
        MessageConfiguration messageConfiguration = configurationManager.create(MessageConfiguration.class, dataFolder);
        GuiConfiguration guiConfiguration = configurationManager.create(GuiConfiguration.class, dataFolder);

        /* Database */
        this.databaseService = new DatabaseService(this.logger, dataFolder, databaseConfiguration);

        this.userCache = new UserCache();

        try {
            this.databaseService.connect();

            this.userRepository = new DaoUserRepositoryImpl(this.databaseService.getConnectionSource(), this.userCache);
        }
        catch (SQLException sqlException) {
            this.userRepository = new EmptyUserRepositoryImpl();

            this.logger.log(Level.SEVERE, "An error occurred while trying to initialize database. The plugin will run, but the functions will not work as expected. ", sqlException);
        }

        BukkitSpentTime bukkitSpentTime = new BukkitSpentTime(this.server);

        /* Services */
        UserService userService = new UserService(this.logger, this.userRepository, this.userCache, bukkitSpentTime);
        UpdateService updateService = new UpdateService(pluginConfiguration, plugin.getDescription());
        this.messageService = new MessageService(messageConfiguration, BukkitAudiences.create(plugin), MiniMessage.miniMessage());

        /* Tasks */
        TaskScheduler taskScheduler = new BukkitTaskScheduler(plugin, this.server);
        taskScheduler.runTimerAsync(new UserSaveTask(this.server, this.userCache, userService), DurationUtil.toTicks(Duration.ofMinutes(1)), DurationUtil.toTicks(pluginConfiguration.spentTimeSaveDelay));

        /* Guis */
        GuiManager guiManager = new GuiManager(taskScheduler);
        Stream.of(
                new ConfirmationGui(guiConfiguration, guiConfiguration.items, taskScheduler),
                new SpentTimeTopGui(this.logger, this.server, guiConfiguration, guiConfiguration.items, userService, this.userRepository, this.messageService, guiManager, taskScheduler)
        ).forEach(guiManager::registerGui);

        /* Listeners */
        Stream.of(
                new UserCreateController(this.server, userService),
                new UserSaveController(this.userCache, userService),
                new UserUpdateController(this.userCache, userService),
                new UpdateController(this.logger, pluginConfiguration, this.messageService, updateService, taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* Commands */
        this.liteCommands = LiteBukkitFactory.builder("SpentTime", plugin, this.server)
                .argument(Player.class, new PlayerArgument(this.server, messageConfiguration))
                .argument(User.class, new UserArgument(this.userCache, this.userRepository, messageConfiguration))

                .context(Player.class, new PlayerContextual())

                .missingPermission(new MissingPermissionHandler(this.messageService))
                .invalidUsage(new UsageHandler(this.messageService))

                .commands(
                        new TimeCommand(this.logger, userService, this.messageService, bukkitSpentTime),
                        new ResetAllCommand(this.logger, this.messageService, this.userRepository, bukkitSpentTime, guiManager),
                        new ResetCommand(this.logger, userService, this.messageService, guiManager),
                        new SetCommand(this.logger, userService, this.messageService, bukkitSpentTime),
                        new TopCommand(this.logger, pluginConfiguration, this.userRepository, this.messageService, guiManager)
                )
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

    void disable() {
        SpentTimeApiProvider.unregister();

        for (Player player : this.server.getOnlinePlayers()) {
            this.closeGui(player);
            this.saveUser(player);
        }

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
     * Closes custom GUI if the player has one open.
     */
    void closeGui(@NotNull Player player) {
        InventoryView openInventory = player.getOpenInventory();
        Inventory topInventory = openInventory.getTopInventory();

        if (!(topInventory.getHolder() instanceof BaseGui)) {
            return;
        }

        player.closeInventory();
    }


    /**
     * Saves player data if present in cache.
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
