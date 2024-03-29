package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.command.argument.PlayerArgument;
import com.github.imdmk.spenttime.command.contextual.PlayerContextual;
import com.github.imdmk.spenttime.command.handler.MissingPermissionHandler;
import com.github.imdmk.spenttime.command.handler.NotificationHandler;
import com.github.imdmk.spenttime.command.handler.UsageHandler;
import com.github.imdmk.spenttime.command.implementation.SpentTimeCommand;
import com.github.imdmk.spenttime.command.implementation.SpentTimeResetAllCommand;
import com.github.imdmk.spenttime.command.implementation.SpentTimeResetCommand;
import com.github.imdmk.spenttime.command.implementation.SpentTimeSetCommand;
import com.github.imdmk.spenttime.command.implementation.SpentTimeTopCommand;
import com.github.imdmk.spenttime.configuration.ConfigurationFactory;
import com.github.imdmk.spenttime.configuration.implementation.PluginConfiguration;
import com.github.imdmk.spenttime.database.DatabaseManager;
import com.github.imdmk.spenttime.gui.implementation.SpentTimeTopGui;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.placeholder.PlaceholderRegistry;
import com.github.imdmk.spenttime.placeholder.SpentTimePlaceholder;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.scheduler.TaskSchedulerImpl;
import com.github.imdmk.spenttime.update.UpdateListener;
import com.github.imdmk.spenttime.update.UpdateService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
import com.github.imdmk.spenttime.user.argument.UserArgument;
import com.github.imdmk.spenttime.user.listener.UserCreateListener;
import com.github.imdmk.spenttime.user.listener.UserLoadListener;
import com.github.imdmk.spenttime.user.listener.UserSaveListener;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.user.repository.impl.UserEmptyRepositoryImpl;
import com.github.imdmk.spenttime.user.repository.impl.UserRepositoryImpl;
import com.github.imdmk.spenttime.user.task.UserSpentTimeSaveTask;
import com.github.imdmk.spenttime.util.DurationUtil;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.triumphteam.gui.guis.BaseGui;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SpentTime {

    private final Plugin plugin;
    private final Server server;

    private final PluginConfiguration pluginConfiguration;

    private final DatabaseManager databaseManager;

    private UserRepository userRepository;
    private final UserManager userManager;

    private final BukkitAudiences bukkitAudiences;
    private final NotificationSender notificationSender;

    private final TaskScheduler taskScheduler;

    private final SpentTimeTopGui spentTimeTopGui;

    private final LiteCommands<CommandSender> liteCommands;

    private PlaceholderRegistry placeholderRegistry;

    private final Metrics metrics;

    public SpentTime(Plugin plugin) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        File dataFolder = plugin.getDataFolder();
        Logger logger = plugin.getLogger();

        this.plugin = plugin;
        this.server = plugin.getServer();

        /* Configuration */
        this.pluginConfiguration = ConfigurationFactory.create(PluginConfiguration.class, new File(dataFolder, "configuration.yml"));

        /* Database */
        this.databaseManager = new DatabaseManager(logger, dataFolder, this.pluginConfiguration.databaseSettings);

        try {
            this.databaseManager.connect();

            this.userRepository = new UserRepositoryImpl(logger, this.databaseManager.getConnectionSource());
        }
        catch (SQLException sqlException) {
            this.userRepository = new UserEmptyRepositoryImpl();

            logger.log(Level.SEVERE, "An error occurred while trying to initialize database. The plugin will run, but the functions will not work as expected. ", sqlException);
        }

        this.userManager = new UserManager(this.userRepository);

        /* Services */
        UpdateService updateService = new UpdateService(plugin.getDescription());

        /* Adventure */
        this.bukkitAudiences = BukkitAudiences.create(plugin);
        this.notificationSender = new NotificationSender(this.bukkitAudiences);

        /* Tasks */
        this.taskScheduler = new TaskSchedulerImpl(plugin, this.server);
        this.taskScheduler.runTimerAsync(new UserSpentTimeSaveTask(this.server, this.userRepository, this.userManager), DurationUtil.toTicks(Duration.ofMinutes(1)), DurationUtil.toTicks(this.pluginConfiguration.spentTimeSaveDelay));

        /* Guis */
        this.spentTimeTopGui = new SpentTimeTopGui(this.server, this.pluginConfiguration.notificationSettings, this.pluginConfiguration.guiSettings, this.pluginConfiguration.scrollingGuiSettings, this.pluginConfiguration.guiSettings.guiItemSettings, this.notificationSender, this.userRepository, this.taskScheduler);

        /* Listeners */
        Stream.of(
            new UserCreateListener(this.userRepository, this.userManager, this.taskScheduler),
            new UserLoadListener(this.server, this.userManager),
            new UserSaveListener(this.userManager, this.userRepository, this.taskScheduler),
            new UpdateListener(logger, this.pluginConfiguration, this.notificationSender, updateService, this.taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* Commands */
        this.liteCommands = this.registerLiteCommands();

        /* PlaceholderAPI */
        if (this.server.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderRegistry = new PlaceholderRegistry();

            Stream.of(
                    new SpentTimePlaceholder(plugin.getDescription())
            ).forEach(this.placeholderRegistry::register);
        }

        /* Metrics */
        this.metrics = new Metrics((JavaPlugin) plugin, 19362);

        logger.info("Enabled plugin in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms.");
    }

    public void onDisable() {
        for (Player player : this.server.getOnlinePlayers()) {
            this.closeGui(player);
            this.saveUser(player);
        }

        if (this.databaseManager != null) {
            this.databaseManager.close();
        }

        this.bukkitAudiences.close();
        this.liteCommands.unregister();

        if (this.placeholderRegistry != null) {
            this.placeholderRegistry.unregisterAll();
        }

        this.metrics.shutdown();
    }

    private LiteCommands<CommandSender> registerLiteCommands() {
        return LiteBukkitFactory.builder("SpentTime", this.plugin, this.server)
                .argument(Player.class, new PlayerArgument(this.server, this.pluginConfiguration.notificationSettings))
                .argument(User.class, new UserArgument(this.pluginConfiguration.notificationSettings, this.userManager))

                .context(Player.class, new PlayerContextual())

                .missingPermission(new MissingPermissionHandler(this.pluginConfiguration.notificationSettings, this.notificationSender))
                .result(Notification.class, new NotificationHandler(this.notificationSender))
                .invalidUsage(new UsageHandler(this.pluginConfiguration.notificationSettings, this.notificationSender))

                .commands(
                        new SpentTimeCommand(this.pluginConfiguration.notificationSettings, this.notificationSender),
                        new SpentTimeResetAllCommand(this.server, this.pluginConfiguration.notificationSettings, this.userRepository, this.notificationSender, this.taskScheduler),
                        new SpentTimeResetCommand(this.server, this.pluginConfiguration.notificationSettings, this.userRepository, this.notificationSender, this.taskScheduler),
                        new SpentTimeSetCommand(this.server, this.pluginConfiguration.notificationSettings, this.userRepository, this.notificationSender, this.taskScheduler),
                        new SpentTimeTopCommand(this.pluginConfiguration.guiSettings, this.pluginConfiguration.notificationSettings, this.userRepository, this.notificationSender, this.spentTimeTopGui)
                )
                .build();
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
        this.userManager.getUser(player.getUniqueId()).ifPresent(this.userRepository::save);
    }
}
