package com.github.imdmk.spenttime.user.task;

import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UserSaveTask implements Runnable {

    private final Server server;
    private final UserCache userCache;
    private final UserService userService;

    public UserSaveTask(Server server, UserCache userCache, UserService userService) {
        this.server = server;
        this.userCache = userCache;
        this.userService = userService;
    }

    @Override
    public void run() {
        this.server.getOnlinePlayers().forEach(this::updateUser);
    }

    private void updateUser(@NotNull Player player) {
        this.userCache.get(player.getUniqueId()).ifPresent(user -> this.userService.updateUser(player, user));
    }
}
