package com.github.imdmk.spenttime.user.task;

import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserSaveTask implements Runnable {

    private final Server server;
    private final UserCache userCache;
    private final UserService userService;

    public UserSaveTask(Server server, UserCache userCache, UserService userService) {
        this.server = Objects.requireNonNull(server, "server cannot be null");
        this.userCache = Objects.requireNonNull(userCache, "userCache cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
    }

    @Override
    public void run() {
        this.server.getOnlinePlayers().forEach(this::updateUser);
    }

    private void updateUser(@NotNull Player player) {
        this.userCache.getUserByUuid(player.getUniqueId()).ifPresent(user -> this.userService.updateUser(player, user));
    }
}
