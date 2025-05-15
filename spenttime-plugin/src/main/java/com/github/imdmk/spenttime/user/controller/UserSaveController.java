package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class UserSaveController implements Listener {

    private final UserCache userCache;
    private final UserService userService;

    public UserSaveController(
            @NotNull UserCache userCache,
            @NotNull UserService userService
    ) {
        this.userCache = userCache;
        this.userService = userService;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.userCache.get(event.getPlayer().getUniqueId()).ifPresent(this.userService::saveUser);
    }
}
