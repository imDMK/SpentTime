package com.github.imdmk.spenttime.litecommands.argument;

import com.github.imdmk.spenttime.notification.NotificationSettings;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class UserArgument extends ArgumentResolver<CommandSender, User> {

    private final NotificationSettings notificationSettings;
    private final UserService userService;
    private final UserCache userCache;

    public UserArgument(NotificationSettings notificationSettings, UserService userService, UserCache userCache) {
        this.notificationSettings = notificationSettings;
        this.userService = userService;
        this.userCache = userCache;
    }

    @Override
    protected ParseResult<User> parse(Invocation<CommandSender> senderInvocation, Argument<User> context, String argument) {
        Optional<User> userOptional = this.userService.getOrFindUser(argument);

        return userOptional.map(ParseResult::success)
                .orElseGet(() -> ParseResult.failure(this.notificationSettings.playerNotFound));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<User> argument, SuggestionContext context) {
        return SuggestionResult.of(this.userCache.getUserNames());
    }
}
