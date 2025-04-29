package com.github.imdmk.spenttime.litecommands.argument;

import com.github.imdmk.spenttime.message.MessageConfiguration;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlayerArgument extends ArgumentResolver<CommandSender, Player> {

    private final Server server;
    private final MessageConfiguration messageConfiguration;

    public PlayerArgument(Server server, MessageConfiguration messageConfiguration) {
        this.server = server;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    protected ParseResult<Player> parse(Invocation<CommandSender> invocation, Argument<Player> context, String argument) {
        return Optional.ofNullable(this.server.getPlayer(argument))
                .map(ParseResult::success)
                .orElseGet(() -> ParseResult.failure(this.messageConfiguration.playerNotFound));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Player> argument, SuggestionContext context) {
        return this.server.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(SuggestionResult.collector());
    }
}
