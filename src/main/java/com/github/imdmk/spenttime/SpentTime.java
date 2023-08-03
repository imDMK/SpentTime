package com.github.imdmk.spenttime;

import com.eternalcode.gitcheck.git.GitException;
import com.github.imdmk.spenttime.command.SpentTimeCommand;
import com.github.imdmk.spenttime.command.argument.PlayerArgument;
import com.github.imdmk.spenttime.command.editor.SpentTimeCommandEditor;
import com.github.imdmk.spenttime.command.handler.MissingPermissionHandler;
import com.github.imdmk.spenttime.command.handler.NotificationHandler;
import com.github.imdmk.spenttime.command.handler.UsageHandler;
import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.configuration.serializer.pack.SpentTimePack;
import com.github.imdmk.spenttime.database.DatabaseManager;
import com.github.imdmk.spenttime.gui.TopSpentTimeGui;
import com.github.imdmk.spenttime.gui.TopSpentTimePaginatedGui;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.task.TaskScheduler;
import com.github.imdmk.spenttime.task.TaskSchedulerImpl;
import com.github.imdmk.spenttime.update.UpdateService;
import com.github.imdmk.spenttime.user.UserManager;
import com.github.imdmk.spenttime.user.listener.UserCreateListener;
import com.github.imdmk.spenttime.user.listener.UserSaveListener;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import com.github.imdmk.spenttime.user.repository.impl.UserEmptyRepositoryImpl;
import com.github.imdmk.spenttime.user.repository.impl.UserRepositoryImpl;
import com.github.imdmk.spenttime.util.AnsiColor;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.platform.LiteBukkitAdventurePlatformFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
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

    private final TopSpentTimeGui topSpentTimeGui;
    private final TopSpentTimePaginatedGui topSpentTimePaginatedGui;

    private final LiteCommands<CommandSender> liteCommands;

    public SpentTime(Plugin plugin) {
        Instant start = Instant.now();
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
        this.notificationSender = new NotificationSender(this.bukkitAudiences, MiniMessage.miniMessage());

        /* Tasks */
        this.taskScheduler = new TaskSchedulerImpl(plugin, this.server);

        /* Guis */
        this.topSpentTimeGui = new TopSpentTimeGui(this.server, this.pluginConfiguration.guiConfiguration, this.taskScheduler);
        this.topSpentTimePaginatedGui = new TopSpentTimePaginatedGui(this.server, this.pluginConfiguration.guiConfiguration, this.taskScheduler);

        /* Listeners */
        Stream.of(
            new UserCreateListener(this.userManager, this.taskScheduler),
            new UserSaveListener(this.userManager, this.userRepository, this.taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* Commands */
        this.liteCommands = this.registerLiteCommands();

        /* Update check */
        if (this.pluginConfiguration.checkForUpdate) {
            String version = plugin.getDescription().getVersion();

            try {
                new UpdateService(version, this.logger).check();
            }
            catch (GitException gitException) {
                this.logger.info(AnsiColor.RED + "An error occurred while checking for update: " + gitException.getMessage() + AnsiColor.RESET);
            }
        }

        /* Metrics */
        new Metrics((JavaPlugin) plugin, 19362);

        Duration timeElapsed = Duration.between(start, Instant.now());
        this.logger.info("Enabled plugin in " + timeElapsed.toMillis() + "ms.");
    }

    public void onDisable() {
        if (this.databaseManager != null) {
            this.databaseManager.close();
        }

        if (this.bukkitAudiences != null) {
            this.bukkitAudiences.close();
        }

        if (this.liteCommands != null) {
            this.liteCommands.getPlatform().unregisterAll();
        }

        this.logger.info("GoodBye...");
    }

    private PluginConfiguration createConfiguration(File dataFolder) {
        return ConfigManager.create(PluginConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SpentTimePack(), new SerdesCommons());
            it.withBindFile(new File(dataFolder, "configuration.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    private LiteCommands<CommandSender> registerLiteCommands() {
        return LiteBukkitAdventurePlatformFactory.builder(this.server, "SpentTime", false, this.bukkitAudiences, true)
                .argument(Player.class, new PlayerArgument(this.server, this.pluginConfiguration.messageConfiguration))

                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("Only player can use this command."))

                .permissionHandler(new MissingPermissionHandler(this.pluginConfiguration.messageConfiguration, this.notificationSender))
                .resultHandler(Notification.class, new NotificationHandler(this.notificationSender))
                .invalidUsageHandler(new UsageHandler(this.pluginConfiguration.messageConfiguration, this.notificationSender))

                .commandInstance(
                        new SpentTimeCommand(this.pluginConfiguration.guiConfiguration, this.pluginConfiguration.messageConfiguration, this.userRepository, this.userManager, this.notificationSender, this.topSpentTimeGui, this.topSpentTimePaginatedGui)
                )

                .commandEditor(SpentTimeCommand.class, new SpentTimeCommandEditor(this.pluginConfiguration))

                .register();
    }
}
