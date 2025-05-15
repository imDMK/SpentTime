package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserUpdateController implements Listener {

    private final UserCache userCache;
    private final UserService userService;

    public UserUpdateController(UserCache userCache, UserService userService) {
        this.userCache = userCache;
        this.userService = userService;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.userCache.get(player.getUniqueId())
                .ifPresent(user -> this.userService.updateUser(player, user));
    }
}
