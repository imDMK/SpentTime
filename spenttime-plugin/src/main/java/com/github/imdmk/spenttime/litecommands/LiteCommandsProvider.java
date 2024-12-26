package com.github.imdmk.spenttime.litecommands;

import com.github.imdmk.spenttime.configuration.PluginConfiguration;
import com.github.imdmk.spenttime.gui.SpentTimeTopGui;
import com.github.imdmk.spenttime.litecommands.argument.PlayerArgument;
import com.github.imdmk.spenttime.litecommands.contextual.PlayerContextual;
import com.github.imdmk.spenttime.litecommands.handler.MissingPermissionHandler;
import com.github.imdmk.spenttime.litecommands.handler.UsageHandler;
import com.github.imdmk.spenttime.litecommands.implementation.SpentTimeCommand;
import com.github.imdmk.spenttime.litecommands.implementation.SpentTimeResetAllCommand;
import com.github.imdmk.spenttime.litecommands.implementation.SpentTimeResetCommand;
import com.github.imdmk.spenttime.litecommands.implementation.SpentTimeSetCommand;
import com.github.imdmk.spenttime.litecommands.implementation.SpentTimeTopCommand;
import com.github.imdmk.spenttime.notification.Notification;
import com.github.imdmk.spenttime.notification.NotificationHandler;
import com.github.imdmk.spenttime.notification.NotificationSender;
import com.github.imdmk.spenttime.scheduler.TaskScheduler;
import com.github.imdmk.spenttime.user.BukkitPlayerSpentTimeService;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import com.github.imdmk.spenttime.user.argument.UserArgument;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LiteCommandsProvider {

    private final Plugin plugin;
    private final Server server;
    private final PluginConfiguration pluginConfiguration;
    private final UserCache userCache;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationSender notificationSender;
    private final TaskScheduler taskScheduler;
    private final SpentTimeTopGui spentTimeTopGui;
    private final BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService;

    public LiteCommandsProvider(Plugin plugin, Server server, PluginConfiguration pluginConfiguration, UserCache userCache, UserRepository userRepository, UserService userService, NotificationSender notificationSender, TaskScheduler taskScheduler, SpentTimeTopGui spentTimeTopGui, BukkitPlayerSpentTimeService bukkitPlayerSpentTimeService) {
        this.plugin = plugin;
        this.server = server;
        this.pluginConfiguration = pluginConfiguration;
        this.userCache = userCache;
        this.userRepository = userRepository;
        this.userService = userService;
        this.notificationSender = notificationSender;
        this.taskScheduler = taskScheduler;
        this.spentTimeTopGui = spentTimeTopGui;
        this.bukkitPlayerSpentTimeService = bukkitPlayerSpentTimeService;
    }

    public LiteCommands<CommandSender> register() {
        return LiteBukkitFactory.builder("SpentTime", this.plugin, this.server)
                .argument(Player.class, new PlayerArgument(this.server, this.pluginConfiguration.notificationSettings))
                .argument(User.class, new UserArgument(this.pluginConfiguration.notificationSettings, this.userService, this.userCache))

                .context(Player.class, new PlayerContextual())

                .missingPermission(new MissingPermissionHandler(this.notificationSender, this.pluginConfiguration.notificationSettings))
                .result(Notification.class, new NotificationHandler(this.notificationSender))
                .invalidUsage(new UsageHandler(this.pluginConfiguration.notificationSettings, this.notificationSender))

                .commands(
                        new SpentTimeCommand(this.pluginConfiguration.notificationSettings, this.notificationSender, this.bukkitPlayerSpentTimeService),
                        new SpentTimeResetAllCommand(this.server, this.pluginConfiguration.notificationSettings, this.userRepository, this.notificationSender, this.taskScheduler, this.bukkitPlayerSpentTimeService),
                        new SpentTimeResetCommand(this.pluginConfiguration.notificationSettings, this.userRepository, this.notificationSender, this.taskScheduler, this.bukkitPlayerSpentTimeService),
                        new SpentTimeSetCommand(this.pluginConfiguration.notificationSettings, this.userRepository, this.notificationSender, this.bukkitPlayerSpentTimeService),
                        new SpentTimeTopCommand(this.pluginConfiguration.guiSettings, this.pluginConfiguration.notificationSettings, this.userRepository, this.notificationSender, this.spentTimeTopGui)
                )
                .build();
    }
}
