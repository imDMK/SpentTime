package com.github.imdmk.spenttime.user;

import com.github.imdmk.spenttime.infrastructure.message.MessageConfiguration;
import com.github.imdmk.spenttime.user.repository.UserRepository;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserArgument extends ArgumentResolver<CommandSender, User> {

    private final UserCache userCache;
    private final UserRepository userRepository;
    private final MessageConfiguration messageConfiguration;

    public UserArgument(
            @NotNull UserCache userCache,
            @NotNull UserRepository userRepository,
            @NotNull MessageConfiguration messageConfiguration
    ) {
        this.userCache = Objects.requireNonNull(userCache, "userCache cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.messageConfiguration = Objects.requireNonNull(messageConfiguration, "messageConfiguration cannot be null");
    }

    @Override
    protected ParseResult<User> parse(Invocation<CommandSender> senderInvocation, Argument<User> context, String argument) {
        return this.userRepository.findByNameDirect(argument)
                .map(ParseResult::success)
                .orElseGet(() -> ParseResult.failure(this.messageConfiguration.playerNotFound));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<User> argument, SuggestionContext context) {
        return SuggestionResult.of(this.userCache.getAllCachedUserNames());
    }
}
