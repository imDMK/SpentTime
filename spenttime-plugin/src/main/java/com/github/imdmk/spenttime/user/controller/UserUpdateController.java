package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class UserUpdateController implements Listener {

    private final UserCache userCache;
    private final UserService userService;

    public UserUpdateController(UserCache userCache, UserService userService) {
        this.userCache = Objects.requireNonNull(userCache, "userCache cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.userCache.getUserByUuid(player.getUniqueId())
                .ifPresent(user -> this.userService.updateUser(player, user));
    }
}
