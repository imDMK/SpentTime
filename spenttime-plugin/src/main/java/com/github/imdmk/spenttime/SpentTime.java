package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.configuration.ConfigurationFactory;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.database.DatabaseService;
import com.github.imdmk.spenttime.gui.SpentTimeTopGui;
import com.github.imdmk.spenttime.litecommands.LiteCommandsProvider;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.placeholder.PlaceholderRegistry;
import com.github.imdmk.spenttime.placeholder.SpentTimePlaceholder;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.scheduler.TaskSchedulerImpl;
import com.github.imdmk.spenttime.update.UpdateController;
import com.github.imdmk.spenttime.update.UpdateService;
import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.controller.UserCreateController;
import com.github.imdmk.spenttime.user.controller.UserLoadController;
import com.github.imdmk.spenttime.user.controller.UserSaveController;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.user.repository.impl.DaoUserRepositoryImpl;
import com.github.imdmk.spenttime.user.repository.impl.EmptyUserRepositoryImpl;
import com.github.imdmk.spenttime.user.task.UserSaveSpentTimeTask;
import com.github.imdmk.spenttime.util.DurationUtil;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.triumphteam.gui.guis.BaseGui;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SpentTime implements SpentTimeApi {

    private final Server server;

    private final DatabaseService databaseService;

    private final UserCache userCache;
    private UserRepository userRepository;
    private final UserService userService;

    private final BukkitAudiences bukkitAudiences;

    private final LiteCommands<CommandSender> liteCommands;

    private PlaceholderRegistry placeholderRegistry;

    private final Metrics metrics;

    public SpentTime(Plugin plugin) {
        SpentTimeApiProvider.register(this);

        Stopwatch stopwatch = Stopwatch.createStarted();
        File dataFolder = plugin.getDataFolder();
        Logger logger = plugin.getLogger();

        this.server = plugin.getServer();

        /* Configuration */
        PluginConfiguration pluginConfiguration = ConfigurationFactory.create(PluginConfiguration.class, new File(dataFolder, "configuration.yml"));

        /* Database */
        this.databaseService = new DatabaseService(logger, dataFolder, pluginConfiguration.databaseSettings);

        this.userCache = new UserCache();

        try {
            this.databaseService.connect();

            this.userRepository = new DaoUserRepositoryImpl(this.databaseService.getConnectionSource(), this.userCache);
        }
        catch (SQLException sqlException) {
            this.userRepository = new EmptyUserRepositoryImpl();

            logger.log(Level.SEVERE, "An error occurred while trying to initialize database. The plugin will run, but the functions will not work as expected. ", sqlException);
        }

        this.userService = new UserService(this.userRepository, this.userCache);
        BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService = new BukkitPlayerSpentTimeService(this.server);

        /* Adventure */
        this.bukkitAudiences = BukkitAudiences.create(plugin);
        NotificationSender notificationSender = new NotificationSender(this.bukkitAudiences);

        /* Tasks */
        TaskScheduler taskScheduler = new TaskSchedulerImpl(plugin, this.server);
        taskScheduler.runTimerAsync(new UserSaveSpentTimeTask(this.server, this.userRepository, this.userCache, bukkitPlayerSpentTimeService), DurationUtil.toTicks(Duration.ofMinutes(1)), DurationUtil.toTicks(pluginConfiguration.spentTimeSaveDelay));

        /* Guis */
        SpentTimeTopGui spentTimeTopGui = new SpentTimeTopGui(this.server, pluginConfiguration.notificationSettings, pluginConfiguration.guiSettings, pluginConfiguration.scrollingGuiSettings, pluginConfiguration.guiSettings.guiItemSettings, notificationSender, this.userRepository, taskScheduler, bukkitPlayerSpentTimeService);

        /* Update Service */
        UpdateService updateService = new UpdateService(plugin.getDescription());

        /* Listeners */
        Stream.of(
                new UserCreateController(this.userRepository, this.userService, bukkitPlayerSpentTimeService),
                new UserLoadController(this.server, this.userRepository),
                new UserSaveController(this.userCache, this.userRepository, bukkitPlayerSpentTimeService),
                new UpdateController(logger, pluginConfiguration, notificationSender, updateService, taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* Commands */
        LiteCommandsProvider liteCommandsProvider = new LiteCommandsProvider(plugin, this.server, pluginConfiguration, this.userCache, this.userRepository, this.userService, notificationSender, taskScheduler, spentTimeTopGui, bukkitPlayerSpentTimeService);
        this.liteCommands = liteCommandsProvider.register();

        /* PlaceholderAPI */
        if (this.server.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderRegistry = new PlaceholderRegistry();

            Stream.of(
                    new SpentTimePlaceholder(plugin.getDescription(), bukkitPlayerSpentTimeService)
            ).forEach(this.placeholderRegistry::register);
        }

        /* Metrics */
        this.metrics = new Metrics(plugin, 19362);

        logger.info("Enabled plugin in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms.");
    }

    public void onDisable() {
        SpentTimeApiProvider.unregister();

        for (Player player : this.server.getOnlinePlayers()) {
            this.closeGui(player);
            this.saveUser(player);
        }

        if (this.databaseService != null) {
            this.databaseService.close();
        }

        this.bukkitAudiences.close();
        this.liteCommands.unregister();

        if (this.placeholderRegistry != null) {
            this.placeholderRegistry.unregisterAll();
        }

        this.metrics.shutdown();
    }

    private void closeGui(Player player) {
        InventoryView openInventory = player.getOpenInventory();
        Inventory topInventory = openInventory.getTopInventory();

        if (!(topInventory.getHolder() instanceof BaseGui)) {
            return;
        }

        player.closeInventory();
    }

    private void saveUser(Player player) {
        this.userCache.get(player.getUniqueId()).ifPresent(this.userRepository::save);
    }

    @Override
    public UserCache getUserCache() {
        return this.userCache;
    }

    @Override
    public UserService getUserService() {
        return this.userService;
    }

    @Override
    public UserRepository getUserRepository() {
        return this.userRepository;
    }
}
