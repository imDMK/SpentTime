package com.github.imdmk.spenttime.command.argument;

import com.github.imdmk.spenttime.configuration.MessageConfiguration;
import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import panda.std.Result;

import java.util.List;
import java.util.Optional;

public class UserArgument implements OneArgument<User> {

    private final UserManager userManager;
    private final MessageConfiguration messageConfiguration;

    public UserArgument(UserManager userManager, MessageConfiguration messageConfiguration) {
        this.userManager = userManager;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public Result<User, ?> parse(LiteInvocation liteInvocation, String argument) {
        Optional<User> userOptional = this.userManager.getOrFindUser(argument);

        return userOptional.map(Result::ok)
                .orElseGet(() -> Result.error(this.messageConfiguration.playerNotFoundNotification));
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return Suggestion.of(this.userManager.getNameUserCache());
    }
}
