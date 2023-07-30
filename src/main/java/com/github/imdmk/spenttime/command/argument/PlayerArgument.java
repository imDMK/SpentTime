package com.github.imdmk.spenttime.command.argument;

import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Result;

import java.util.List;
import java.util.Optional;

public class PlayerArgument implements OneArgument<Player> {

    private final Server server;
    private final MessageConfiguration messageConfiguration;

    public PlayerArgument(Server server, MessageConfiguration messageConfiguration) {
        this.server = server;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public Result<Player, ?> parse(LiteInvocation liteInvocation, String argument) {
        Optional<Player> playerOptional = Optional.ofNullable(this.server.getPlayerExact(argument));

        return playerOptional.map(Result::ok)
                .orElseGet(() -> Result.error(this.messageConfiguration.playerNotFoundNotification));
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return this.server.getOnlinePlayers().stream()
                .map(Player::getName)
                .map(Suggestion::of)
                .toList();
    }
}
