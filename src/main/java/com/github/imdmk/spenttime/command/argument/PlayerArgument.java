package com.github.imdmk.spenttime.command.argument;

import com.github.imdmk.spenttime.notification.configuration.NotificationSettings;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerArgument extends ArgumentResolver<CommandSender, Player> {

    private final Server server;
    private final NotificationSettings notificationSettings;

    public PlayerArgument(Server server, NotificationSettings notificationSettings) {
        this.server = server;
        this.notificationSettings = notificationSettings;
    }

    @Override
    protected ParseResult<Player> parse(Invocation<CommandSender> invocation, Argument<Player> context, String argument) {
        Player player = this.server.getPlayer(argument);

        if (player == null) {
            return ParseResult.failure(this.notificationSettings.playerNotFound);
        }

        return ParseResult.success(player);
    }
}
