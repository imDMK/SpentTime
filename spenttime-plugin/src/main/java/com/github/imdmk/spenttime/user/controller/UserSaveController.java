package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.UserCache;
import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserSaveController implements Listener {

    private final UserCache userCache;
    private final UserService userService;

    public UserSaveController(@NotNull UserCache userCache, @NotNull UserService userService) {
        this.userCache = Objects.requireNonNull(userCache, "userCache cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.userCache.getUserByUuid(event.getPlayer().getUniqueId()).ifPresent(this.userService::saveUser);
    }
}
