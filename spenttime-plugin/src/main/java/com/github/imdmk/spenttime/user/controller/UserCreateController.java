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
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserCreateController implements Listener {

    private final Logger logger;
    private final Server server;
    private final UserService userService;

    public UserCreateController(@NotNull Logger logger, @NotNull Server server, @NotNull UserService userService) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.server = Objects.requireNonNull(server, "server cannot be null");
        this.userService = Objects.requireNonNull(userService, "userService cannot be null");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPlayerJoin(final PlayerJoinEvent event) {
        this.findOrCreate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onServerReload(final ServerLoadEvent event) {
        if (event.getType() != ServerLoadEvent.LoadType.RELOAD) {
            return;
        }

        this.server.getOnlinePlayers().forEach(this::findOrCreate);
    }

    private void findOrCreate(@NotNull Player player) {
        this.userService.findOrCreateUser(player.getUniqueId(), player.getName())
                .exceptionally(throwable -> {
                    this.logger.log(Level.SEVERE, "An error occurred while trying to find or create the user for " + player.getName(), throwable);
                    return null;
                });
    }
}
