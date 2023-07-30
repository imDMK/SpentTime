package com.github.imdmk.spenttime.command.bind;

import com.github.imdmk.spenttime.user.User;
import com.github.imdmk.spenttime.user.UserManager;
import dev.rollczi.litecommands.command.Invocation;
import dev.rollczi.litecommands.contextual.Contextual;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Result;

public class UserContextual implements Contextual<CommandSender, User> {

    private final UserManager userManager;

    public UserContextual(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Result<User, ?> extract(CommandSender sender, Invocation<CommandSender> invocation) {
        if (!(sender instanceof Player player)) {
            return Result.error("<red>You can't use this command.");
        }

        User user = this.userManager.getOrCreateUser(player.getUniqueId(), player.getName());
        return Result.ok(user);
    }
}
