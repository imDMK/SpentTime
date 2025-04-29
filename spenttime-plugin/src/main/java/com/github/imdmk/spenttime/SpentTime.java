package com.github.imdmk.spenttime;

import com.github.imdmk.spenttime.configuration.ConfigurationManager;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.database.DatabaseConfiguration;
import com.github.imdmk.spenttime.database.DatabaseService;
import com.github.imdmk.spenttime.gui.configuration.GuiConfiguration;
import com.github.imdmk.spenttime.litecommands.argument.PlayerArgument;
import com.github.imdmk.spenttime.litecommands.argument.UserArgument;
import com.github.imdmk.spenttime.litecommands.contextual.PlayerContextual;
import com.github.imdmk.spenttime.litecommands.handler.MissingPermissionHandler;
import com.github.imdmk.spenttime.litecommands.handler.UsageHandler;
import com.github.imdmk.spenttime.litecommands.implementation.SpentTimeCommand;
import com.github.imdmk.spenttime.litecommands.implementation.SpentTimeSetCommand;
import com.github.imdmk.spenttime.litecommands.implementation.SpentTimeTopCommand;
import com.github.imdmk.spenttime.litecommands.implementation.reset.SpentTimeResetAllCommand;
import com.github.imdmk.spenttime.litecommands.implementation.reset.SpentTimeResetCommand;
import com.github.imdmk.spenttime.message.MessageConfiguration;
import com.github.imdmk.spenttime.message.MessageService;
import com.github.imdmk.spenttime.placeholder.PlaceholderRegistry;
import com.github.imdmk.spenttime.placeholder.SpentTimePlaceholder;
import com.github.imdmk.spenttime.scheduler.BukkitTaskScheduler;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.update.UpdateController;
import com.github.imdmk.spenttime.update.UpdateService;
import com.github.imdmk.spenttime.user.BukkitSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserCache;
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

import java.io.File;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SpentTime implements SpentTimeApi {

    private final Plugin plugin;
    private final Server server;
    private final Logger logger;

    private ConfigurationManager configurationManager;

    private final DatabaseService databaseService;

    private final UserCache userCache;
    private UserRepository userRepository;

    private final BukkitAudiences bukkitAudiences;

    private final LiteCommands<CommandSender> liteCommands;

    private PlaceholderRegistry placeholderRegistry;

    private final Metrics metrics;

    public SpentTime(Plugin plugin) {
        SpentTimeApiProvider.register(this);

        Stopwatch stopwatch = Stopwatch.createStarted();
        File dataFolder = plugin.getDataFolder();

        this.plugin = plugin;
        this.server = plugin.getServer();
        this.logger = plugin.getLogger();

        /* Configuration */
        this.configurationManager = new ConfigurationManager();

        PluginConfiguration pluginConfiguration = this.configurationManager.create(PluginConfiguration.class, dataFolder);
        DatabaseConfiguration databaseConfiguration = this.configurationManager.create(DatabaseConfiguration.class, dataFolder);
        MessageConfiguration messageConfiguration = this.configurationManager.create(MessageConfiguration.class, dataFolder);
        GuiConfiguration guiConfiguration = this.configurationManager.create(GuiConfiguration.class, dataFolder);

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

        BukkitSpentTimeService bukkitSpentTimeService = new BukkitSpentTimeService(this.server);

        /* Adventure */
        this.bukkitAudiences = BukkitAudiences.create(plugin);
        MessageService messageService = new MessageService(messageConfiguration, this.bukkitAudiences, MiniMessage.miniMessage());

        /* Tasks */
        TaskScheduler taskScheduler = new BukkitTaskScheduler(plugin, this.server);
        taskScheduler.runTimerAsync(new UserSaveSpentTimeTask(this.server, this.userRepository, this.userCache, bukkitSpentTimeService), DurationUtil.toTicks(Duration.ofMinutes(1)), DurationUtil.toTicks(pluginConfiguration.spentTimeSaveDelay));

        /* Guis */
        com.github.imdmk.spenttime.user.gui.SpentTimeTopGui spentTimeTopGui = new com.github.imdmk.spenttime.user.gui.SpentTimeTopGui(this.server, guiConfiguration.items, guiConfiguration.spentTimeTop, this.userRepository, messageService, taskScheduler, bukkitSpentTimeService);

        /* Update Service */
        UpdateService updateService = new UpdateService(plugin.getDescription());

        /* Listeners */
        Stream.of(
                new UserCreateController(this.userRepository, bukkitSpentTimeService, this.logger),
                new UserLoadController(this.server, this.userRepository, this.logger),
                new UserSaveController(this.userCache, this.userRepository, bukkitSpentTimeService),
                new UpdateController(this.logger, pluginConfiguration, messageService, updateService, taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* Commands */
        this.liteCommands = LiteBukkitFactory.builder("SpentTime", this.plugin, this.server)
                .argument(Player.class, new PlayerArgument(this.server, messageConfiguration))
                .argument(User.class, new UserArgument(this.userCache, this.userRepository, messageConfiguration))

                .context(Player.class, new PlayerContextual())

                .missingPermission(new MissingPermissionHandler(messageService))
                .invalidUsage(new UsageHandler(messageService))

                .commands(
                        new SpentTimeCommand(messageService, bukkitSpentTimeService),
                        new SpentTimeResetAllCommand(this.server, this.userRepository, messageService, taskScheduler, bukkitSpentTimeService),
                        new SpentTimeResetCommand(this.userRepository, messageService, taskScheduler, bukkitSpentTimeService),
                        new SpentTimeSetCommand(this.userRepository, messageService, bukkitSpentTimeService),
                        new SpentTimeTopCommand(pluginConfiguration, this.userRepository, messageService, spentTimeTopGui)
                )
                .build();

        /* PlaceholderAPI */
        if (this.server.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderRegistry = new PlaceholderRegistry();

            Stream.of(
                    new SpentTimePlaceholder(plugin.getDescription(), bukkitSpentTimeService)
            ).forEach(this.placeholderRegistry::register);
        }

        /* Metrics */
        this.metrics = new Metrics(plugin, 19362);

        this.logger.info("Enabled plugin in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms.");
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

        if (this.bukkitAudiences != null) {
            this.bukkitAudiences.close();
        }

        if (this.liteCommands != null) {
            this.liteCommands.unregister();
        }

        if (this.placeholderRegistry != null) {
            this.placeholderRegistry.unregisterAll();
        }

        if (this.metrics != null) {
            this.metrics.shutdown();
        }

        this.logger.info("Successfully disabled plugin.");
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
    public UserRepository getUserRepository() {
        return this.userRepository;
    }
}
