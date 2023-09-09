package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.command.SpentTimeCommand;
import com.github.imdmk.spenttime.command.SpentTimeResetCommand;
import com.github.imdmk.spenttime.command.SpentTimeTopCommand;
import com.github.imdmk.spenttime.command.argument.PlayerArgument;
import com.github.imdmk.spenttime.command.argument.UserArgument;
import com.github.imdmk.spenttime.command.editor.SpentTimeResetCommandEditor;
import com.github.imdmk.spenttime.command.handler.MissingPermissionHandler;
import com.github.imdmk.spenttime.command.handler.NotificationHandler;
import com.github.imdmk.spenttime.command.handler.UsageHandler;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.configuration.serializer.ComponentSerializer;
import com.github.imdmk.spenttime.configuration.serializer.ItemMetaSerializer;
import com.github.imdmk.spenttime.configuration.serializer.ItemStackSerializer;
import com.github.imdmk.spenttime.database.DatabaseManager;
import com.github.imdmk.spenttime.gui.implementation.SpentTimeTopGui;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.notification.NotificationSerializer;
import com.github.imdmk.spenttime.placeholder.PlaceholderRegistry;
import com.github.imdmk.spenttime.placeholder.implementation.SpentTimeFormattedPlaceholder;
import com.github.imdmk.spenttime.placeholder.implementation.SpentTimePlaceholder;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.scheduler.TaskSchedulerImpl;
import com.github.imdmk.spenttime.update.UpdateService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
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
import dev.rollczi.litecommands.bukkit.adventure.platform.LiteBukkitAdventurePlatformFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
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

    private final Logger logger;
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

        this.logger = plugin.getLogger();
        this.server = plugin.getServer();

        /* Configuration */
        this.pluginConfiguration = this.createConfiguration(dataFolder);

        /* Database */
        this.databaseManager = new DatabaseManager(this.logger, dataFolder, this.pluginConfiguration.databaseConfiguration);

        try {
            this.databaseManager.connect();

            this.userRepository = new UserRepositoryImpl(this.logger, this.databaseManager.getConnectionSource());
        }
        catch (SQLException sqlException) {
            this.userRepository = new UserEmptyRepositoryImpl();

            this.logger.log(Level.SEVERE, "An error occurred while trying to initialize database. The plugin will run, but the functions will not work as expected. ", sqlException);
        }

        this.userManager = new UserManager(this.userRepository);

        /* Adventure */
        this.bukkitAudiences = BukkitAudiences.create(plugin);
        this.notificationSender = new NotificationSender(this.bukkitAudiences);

        /* Tasks */
        this.taskScheduler = new TaskSchedulerImpl(plugin, this.server);
        this.taskScheduler.runTimerAsync(new UserSpentTimeSaveTask(this.server, this.userRepository, this.userManager), DurationUtil.toTicks(Duration.ofMinutes(1)), DurationUtil.toTicks(this.pluginConfiguration.playerSpentTimeSaveDuration));

        /* Guis */
        this.spentTimeTopGui = new SpentTimeTopGui(this.server, this.pluginConfiguration, this.pluginConfiguration.messageConfiguration, this.pluginConfiguration.guiConfiguration, this.notificationSender, this.userRepository, this.taskScheduler);

        /* Listeners */
        Stream.of(
            new UserCreateListener(this.userRepository, this.userManager, this.taskScheduler),
            new UserLoadListener(this.server, this.userManager),
            new UserSaveListener(this.userManager, this.userRepository, this.taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* Commands */
        this.liteCommands = this.registerLiteCommands();

        /* PlaceholderAPI */
        if (this.server.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderRegistry = new PlaceholderRegistry();

            Stream.of(
                    new SpentTimeFormattedPlaceholder(plugin.getDescription()),
                    new SpentTimePlaceholder(plugin.getDescription())
            ).forEach(this.placeholderRegistry::register);
        }

        /* Update check */
        if (this.pluginConfiguration.checkForUpdate) {
            UpdateService updateService = new UpdateService(plugin.getDescription(), this.logger);

            this.taskScheduler.runLaterAsync(updateService::check, DurationUtil.toTicks(Duration.ofSeconds(5)));
        }

        /* Metrics */
        this.metrics = new Metrics((JavaPlugin) plugin, 19362);

        this.logger.info("Enabled plugin in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms.");
    }

    public void onDisable() {
        if (this.databaseManager != null) {
            this.databaseManager.close();
        }

        this.bukkitAudiences.close();

        this.liteCommands.getPlatform().unregisterAll();

        if (this.placeholderRegistry != null) {
            this.placeholderRegistry.unregisterAll();
        }

        this.metrics.shutdown();

        this.closeAllPlayersGuis();

        this.logger.info("GoodBye...");
    }

    private PluginConfiguration createConfiguration(File dataFolder) {
        return ConfigManager.create(PluginConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons());
            it.withSerdesPack(registry -> {
                registry.register(new ComponentSerializer());
                registry.register(new ItemMetaSerializer());
                registry.register(new ItemStackSerializer());
                registry.register(new NotificationSerializer());
            });
            it.withBindFile(new File(dataFolder, "configuration.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    private LiteCommands<CommandSender> registerLiteCommands() {
        return LiteBukkitAdventurePlatformFactory.builder(this.server, "SpentTime", false, this.bukkitAudiences, true)
                .argument(Player.class, new PlayerArgument(this.server, this.pluginConfiguration.messageConfiguration))
                .argument(User.class, new UserArgument(this.pluginConfiguration.messageConfiguration, this.userManager))

                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("Only player can use this command."))

                .permissionHandler(new MissingPermissionHandler(this.pluginConfiguration.messageConfiguration, this.notificationSender))
                .resultHandler(Notification.class, new NotificationHandler(this.notificationSender))
                .invalidUsageHandler(new UsageHandler(this.pluginConfiguration.messageConfiguration, this.notificationSender))

                .commandInstance(
                        new SpentTimeCommand(this.server, this.pluginConfiguration.messageConfiguration, this.notificationSender),
                        new SpentTimeResetCommand(this.server, this.pluginConfiguration.messageConfiguration, this.userRepository, this.notificationSender, this.taskScheduler),
                        new SpentTimeTopCommand(this.pluginConfiguration.guiConfiguration, this.pluginConfiguration.messageConfiguration, this.userRepository, this.notificationSender, this.spentTimeTopGui)
                )

                .commandEditor(SpentTimeResetCommand.class, new SpentTimeResetCommandEditor(this.pluginConfiguration))

                .register();
    }

    private void closeAllPlayersGuis() {
        for (Player player : this.server.getOnlinePlayers()) {
            if (player.getOpenInventory().getType() != InventoryType.CHEST) {
                return;
            }

            player.closeInventory();
        }
    }
}
