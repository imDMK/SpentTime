package com.github.imdmk.spenttime.user.controller;

import com.github.imdmk.spenttime.user.UserService;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserCreateController implements Listener {

    private final Server server;
    private final UserService userService;

    public UserCreateController(
            @NotNull Server server,
            @NotNull UserService userService
    ) {
        this.server = Objects.requireNonNull(server, "server cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.userService.findOrCreateUser(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onServerReload(ServerLoadEvent event) {
        if (event.getType() != ServerLoadEvent.LoadType.RELOAD) {
            return;
        }

        this.server.getOnlinePlayers().forEach(this.userService::findOrCreateUser);
    }

}
